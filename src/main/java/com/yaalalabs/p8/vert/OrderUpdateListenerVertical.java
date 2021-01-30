package com.yaalalabs.p8.vert;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import static com.yaalalabs.p8.utils.Events.ORDER_UPDATE;

/*
   In a production setup. this will be listening to an SQS queue. For the prototype we will use an periodic update push
 */

public class OrderUpdateListenerVertical extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger("App.OrderUpdateListenerVertical");

    @Override
    public void start(Promise<Void> p) {
        LOG.info("Starting Order update listener vertical");
//        vertx.setPeriodic(1000, event -> vertx.eventBus().publish(ORDER_UPDATE, getUpdate()));
        p.complete();
    }

    private Object getUpdate() {
        JsonObject jsonObject  = new JsonObject();
        jsonObject.put("status","success");
        return jsonObject;
    }
}

