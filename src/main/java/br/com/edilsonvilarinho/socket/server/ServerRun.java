package br.com.edilsonvilarinho.socket.server;




import br.com.edilsonvilarinho.socket.Emitter;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author edilson.souza
 */
public class ServerRun implements Runnable {

    private final ServerSocket serverSocket;
    private ArrayList<ServerIORunnable> ioRunnable;
    private Emitter emitter;
    private Socket client;

    public ServerRun(ServerSocket serverSocket, ArrayList<ServerIORunnable> ioRunnable, Emitter emitter) {
        this.serverSocket = serverSocket;
        this.ioRunnable = ioRunnable;
        this.emitter = emitter;
    }

    @Override
    public void run() {
        while (true) {
            try {
                client = serverSocket.accept();
                if (client != null) {
                    ioRunnableAdd();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            emitter.emitter(Emitter.EVENT_OPEN, client);
                        }
                    }).start();
                }
            } catch (Exception e) {
                emitter.emitter(Emitter.EVENT_ERROR, e);
                break;
            }
        }
    }

    private synchronized void ioRunnableAdd() {
        ioRunnable.add(new ServerIORunnable(client, emitter, String.format("ip/port %s:%s", client.getInetAddress().getHostName(), client.getPort())));
    }
}
