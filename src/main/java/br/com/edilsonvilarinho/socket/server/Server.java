package br.com.edilsonvilarinho.socket.server;


import br.com.edilsonvilarinho.socket.Emitter;

import java.net.Socket;

public interface Server {

    void send(Socket socket, String message);

    void sendAll(String message);

    boolean close();

    boolean isRun();

    void start();

    void addEvents(Emitter.Event event);

    int sizeClients();
}
