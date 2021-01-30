package com.yaalalabs.p8.vert;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class SubscriptionHandlerVertical extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger("App.SubscriptionHandlerVertical");

    @Override
    public void start(Promise<Void> p) {
        LOG.info("Starting Subscription handler vertical");
        Promise<String> promise = Promise.promise();

        vertx.deployVerticle(new WebSocketHandlerVertical(), promise);
        promise.future().setHandler(event -> {
            if (event.failed()) {
                p.fail(event.cause());
                return;
            }
            p.complete();
        });
        LOG.info("Waiting for supporting verticals to start");
    }
}
