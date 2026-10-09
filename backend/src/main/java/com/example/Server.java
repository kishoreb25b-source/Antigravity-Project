package com.example;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.example.api.RegisterHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {

    private static final int PORT =8080;

    public static void main(String[] args) throws IOException{
        HttpServer server =HttpServer.create(new InetSocketAddress(PORT),0);

        //Register routes
        server.createContext("/api/register", new RegisterHandler());
        server.start();

        System.out.println("=================================================");
        System.out.println("Plain Java Backend HTTP server started!");
        System.out.println("PORT: " + PORT);
        System.out.println("http://localhost:" + PORT + "/api/register");
        System.out.println("=================================================");
    }
    
}
