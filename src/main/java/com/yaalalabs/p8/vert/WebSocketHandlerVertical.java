package com.yaalalabs.p8.vert;

import com.yaalalabs.p8.utils.Utils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.yaalalabs.p8.utils.Events.QUERY_REQUEST;

public class WebSocketHandlerVertical extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger("App.WebSocketHandlerVertical");
    private final Map<String, ServerWebSocket> connectionMap = new HashMap<>();

    @Override
    public void start(final Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();
        server.websocketHandler(this::onWebSocketRequest);
        // start listening to the eventbus
        server.listen(8080,j->{
            if(j.failed()){

            }
        });
        LOG.info("Listening on 4000");
        startPromise.complete();
    }

    private void onListen(AsyncResult<HttpServer> event, Promise<Void> promise) {
        if (event.failed()) {
            promise.fail(event.cause());
            return;
        }
        LOG.info("Web-socket server running on 8080");
        promise.complete();
    }

    private void onWebSocketRequest(ServerWebSocket socket) {
        if (!socket.path().equals("/ws")) {
            socket.reject();
            return;
        }
        String uuid = UUID.randomUUID().toString();
        connectionMap.put(uuid, socket);
        LOG.info("Accepted socket ID [" + uuid + "]");
        socket.handler(event -> onWebSocketMsg(event, socket));
        welcome(socket);
    }

    private void welcome(ServerWebSocket socket) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("message","welcome buddy");
        socket.writeTextMessage(jsonObject.toString());
    }

    private void onWebSocketMsg(Buffer event, ServerWebSocket socket) {
        JsonObject json;
        try {
            json = event.toJsonObject();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            notifyInvalidJson(socket);
            return;
        }
        JsonObject track = new JsonObject();
        track.put("a", Instant.now().toEpochMilli());
        json.put("track", track);
        LOG.info("message received with " + json.toString());
        String type = json.getString("type");
        if(type == null){
            notifyInvalidJson(socket);
            return;
        }
        if(type.equals("subscribe")){
            onSubscriptionMsg(json, socket);
            return;
        }
        if(type.equals("un-subscribe")){
            onUnSubscriptionMsg(json, socket);
            return;
        }
        notifyInvalidJson(socket);
    }

    private void onSubscriptionMsg(JsonObject json, ServerWebSocket socket) {
        /// need to think how to handle the subscription
        JsonObject query = json.getJsonObject("query");
        if(!isValidQuery(query)){
            notifyInvalidJson(socket);
            return;
        }
        DeliveryOptions options = new DeliveryOptions();
        options.setSendTimeout(1000); // 1 second timeout
        Utils.updateTimeTrack(json,"a");
        vertx.eventBus().request(QUERY_REQUEST, json, options, (Handler<AsyncResult<Message<JsonObject>>>) event -> {
            JsonObject obj = event.result().body();
            Utils.updateTimeTrack(obj,"i");
            obj.put("latency", obj.getJsonObject("track").getLong("i") - obj.getJsonObject("track").getLong("a"));
            socket.writeTextMessage(obj.toString());
        });
    }

    private boolean isValidQuery(JsonObject query) {
        return query != null;
    }


    private void onUnSubscriptionMsg(JsonObject json, ServerWebSocket socket) {
        /// need to think how to handle un-subscription
    }

    private void notifyInvalidJson(ServerWebSocket socket){
        JsonObject obj = new JsonObject();
        obj.put("error", "invalid json");
        socket.writeTextMessage(obj.toString());
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {

    }
}
