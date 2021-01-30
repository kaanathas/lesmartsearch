package com.yaalalabs.p8.vert;

import com.yaalalabs.p8.utils.Utils;
import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import static com.yaalalabs.p8.utils.Events.*;

public class QueryHandlerVertical extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger("App.QueryHandlerVertical");

    @Override
    public void start(Promise<Void> p) {
        LOG.info("Starting Subscription handler vertical");
        Promise<String> cacheHandlerDeploy = Promise.promise();
        vertx.deployVerticle(new CacheHandlerVertical(), cacheHandlerDeploy);

        Promise<String> orderUpdateListenerDeploy = Promise.promise();
        vertx.deployVerticle(new OrderUpdateListenerVertical(), orderUpdateListenerDeploy);

        Promise<String> promise = Promise.promise();
        vertx.deployVerticle(new CordaApiHandlerVertical(), promise);

        vertx.eventBus().consumer(ORDER_UPDATE, this::onOrderUpdate);
        vertx.eventBus().consumer(QUERY_REQUEST, this::onQueryRequest);

        CompositeFuture.all(cacheHandlerDeploy.future(), orderUpdateListenerDeploy.future(), promise.future()).setHandler(event -> {
            if (event.failed()) {
                p.fail(event.cause());
                return;
            }
            LOG.info("Complete QueryHandlerVertical init");
            p.complete();
        });
    }

    private <T> void onOrderUpdate(Message<T> event) {
        /// publish message to cache handler
        LOG.info("Update received", event.body());
    }

    private <T> void onQueryRequest(Message<Object> tMessage) {
        JsonObject obj = (JsonObject) tMessage.body();
        Utils.updateTimeTrack(obj,"b");
        String id = getQueryId(obj);
        /// check for the query-time
        Utils.updateTimeTrack(obj,"c");
        vertx.eventBus().request(ORDER_SEARCH, obj, (Handler<AsyncResult<Message<JsonObject>>>) event -> {
            try{
                JsonObject reply = event.result().body();
                Utils.updateTimeTrack(reply,"h");
                tMessage.reply(reply);
            } catch (NullPointerException npe){
                LOG.error(npe.getMessage());
            }
        });
    }

    private String getQueryId(JsonObject query) {
        return "qwerty";
    }
}