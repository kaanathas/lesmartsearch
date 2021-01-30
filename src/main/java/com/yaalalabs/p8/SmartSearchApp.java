package com.yaalalabs.p8;

import com.yaalalabs.p8.vert.MainVerticle;
import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class SmartSearchApp {
    private static final Logger LOG = LoggerFactory.getLogger("App.SmartSearch");

    public static void main(final String[] args) {
        LOG.info("Starting Search Component");
        SmartSearchApp app = new SmartSearchApp();
        app.start();
    }

    private final Vertx vertx;

    private SmartSearchApp() {
        vertx = Vertx.vertx();
    }

    private void start() {
        LOG.info("Start deployment");
        Promise<String> handler = Promise.promise();
        getVertx().deployVerticle(new MainVerticle(), handler);
        handler.future().setHandler(event -> {
            if (event.failed()) {
                LOG.error(event.cause());
                LOG.warn("           SYSTEM FAILED           ");
                System.exit(1);
                return;
            }
            LOG.info("           SYSTEM IS READY           ");
        });
    }

    public final Vertx getVertx() {
        return vertx;
    }
}


// Create an HTTP server which simply returns "Hello World!" to each request.
//        System.out.println("sdsdsdsdsdssdsd");
//        HttpServer server = Vertx.vertx().createHttpServer();
//        server.requestHandler(req -> req.response().end("Hello World!"));
////        server.webSocketHandler(new Handler<ServerWebSocket>() {
////            @Override
////            public void handle(ServerWebSocket serverWebSocket) {
////                System.out.println("Socket received");
////                System.out.println(serverWebSocket.uri());
////                serverWebSocket.accept();
////                serverWebSocket.frameHandler(new Handler<WebSocketFrame>() {
////                    @Override
////                    public void handle(WebSocketFrame webSocketFrame) {
////
////                    }
////                });
////            }
////        });
//        LOG.info("sdsdsdsdsd");
//        System.out.println("da da da da da ");
//        server.listen(8080);
//        Vertx vertex = Vertx.vertx();
//        Router router = Router.router(vertex);
//        SockJSHandler sockJSHandler = SockJSHandler.create(vertex);
//        BridgeOptions bridgeOptions = new BridgeOptions()
//                .addInboundPermitted(new PermittedOptions().setAddress("app.markdown"))
//                .addOutboundPermitted(new PermittedOptions().setAddress("page.saved"));
//        sockJSHandler.bridge(bridgeOptions);
//        router.route("/eventbus/*").handler(sockJSHandler);
//        vertex.deployVerticle("a");