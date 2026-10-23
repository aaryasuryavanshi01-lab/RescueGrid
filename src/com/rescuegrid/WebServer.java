package com.rescuegrid;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

/**
 * Built-in Java HTTP Server to host the RescueGrid Interactive Web UI (index.html)
 * locally at http://localhost:8080 without any external dependencies.
 */
public class WebServer {
    public static void startServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                File file = new File("index.html");
                if (!file.exists()) {
                    String fallback = "RescueGrid Server Running. Place index.html in the project root.";
                    exchange.sendResponseHeaders(200, fallback.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(fallback.getBytes());
                    }
                    return;
                }
                byte[] bytes = Files.readAllBytes(file.toPath());
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
        });
        server.setExecutor(null);
        server.start();
        System.out.println("RescueGrid Web UI running at: http://localhost:" + port);
    }
}
