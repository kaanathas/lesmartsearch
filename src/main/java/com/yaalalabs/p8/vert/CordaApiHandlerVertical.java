package com.yaalalabs.p8.vert;

import com.yaalalabs.p8.utils.Utils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.WebClient;

import static com.yaalalabs.p8.utils.Events.ORDER_SEARCH;

public class CordaApiHandlerVertical extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger("App.CordaApiHandlerVertical");
    private WebClient client;

    CordaApiHandlerVertical() {

    }

    @Override
    public void start(Promise<Void> p) {
        LOG.info("Starting Corda api handler vertical");
        client = WebClient.create(vertx);
        vertx.eventBus().consumer(ORDER_SEARCH, this::onMessage);
        p.complete();
    }

    private <T> void onMessage(Message<T> tMessage) {
        JsonObject jsonObject = (JsonObject) tMessage.body();
        Utils.updateTimeTrack(jsonObject, "d");
        jsonObject.put("status", "success");
        queryApi(jsonObject, tMessage::reply);
    }

    private void queryApi(JsonObject obj, Handler<JsonObject> objectHandler) {
        JsonObject request = obj.getJsonObject("request");
        if (request == null) {
            vertx.setTimer(1, aLong -> {
                Utils.updateTimeTrack(obj, "g");
                objectHandler.handle(obj);
            });
            return;
        }
        Integer port = request.getInteger("port");
        String base = request.getString("base");
        String path = request.getString("path");
        client.post(port, base, path)
                .sendJsonObject(obj, ar -> {
                    if (ar.failed()) {
                        LOG.error(ar.cause().getMessage());
                        return;
                    }
                    JsonObject json = ar.result().body().toJsonObject();
                    Utils.updateTimeTrack(obj, "g");
                    objectHandler.handle(json);
                });

    }
}
