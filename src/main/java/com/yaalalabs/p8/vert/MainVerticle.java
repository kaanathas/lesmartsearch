package com.yaalalabs.p8.vert;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger("App.MainVertical");

    @Override
    public void start(Promise<Void> p) {
        Promise<String> queryHandlerDeploy = Promise.promise();
        vertx.deployVerticle(new QueryHandlerVertical(), queryHandlerDeploy);
        queryHandlerDeploy.future()
                .compose(s -> {
                    Promise<String> subscriptionHandlerDeploy = Promise.promise();
                    vertx.deployVerticle(new SubscriptionHandlerVertical(), subscriptionHandlerDeploy);
                    return subscriptionHandlerDeploy.future();
                }).setHandler(event -> {
            LOG.info("********************************************************************************************");
            if (event.failed()) {
                p.fail(event.cause());
                return;
            }
            p.complete();
        });
    }
}
