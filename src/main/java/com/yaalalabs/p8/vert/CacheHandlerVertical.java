package com.yaalalabs.p8.vert;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class CacheHandlerVertical extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger("App.CacheHandlerVertical");

    @Override
    public void start(Promise<Void> p) {
        LOG.info("Starting Subscription handler vertical");
        p.complete();
    }
}



