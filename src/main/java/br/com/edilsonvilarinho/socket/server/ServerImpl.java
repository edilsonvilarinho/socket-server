package br.com.edilsonvilarinho.socket.server;



import br.com.edilsonvilarinho.socket.Emitter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

/**
 * This class is responsible for implementing the server interface
 */
public class ServerImpl implements Server {

    private static final Logger logger = Logger.getLogger(Socket.class.getName());

    protected final ServerSocket serverSocket;
    protected final ArrayList<ServerIORunnable> socketsClients;
    protected Thread threadServerSocket;
    protected Emitter emitter;
    protected int milliseconds = 3000;

    /**
     * Responsible for receiving the port where the server will listen
     *
     * @param port the port on which the server will listen
     * @throws Exception in case any generic exception
     */
    public ServerImpl(int port) throws Exception {
        serverSocket = new ServerSocket(port);
        emitter = new Emitter();
        socketsClients = new ArrayList<>();
        emitter.add(Emitter.EVENT_CLIENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ServerImpl.this.removeSocketClient((Socket) args[0]);
            }
        });
    }

    /**
     * Responsible for listening to clients on the server
     */
    @Override
    public void start() {
        ServerRun serverRun = new ServerRun(serverSocket, socketsClients, emitter);
        threadServerSocket = new Thread(serverRun);
        threadServerSocket.start();
    }

    /**
     * Responsible for adding the events that will be observed by the server
     *
     * @param event will be added to the server
     */
    @Override
    public void addEvents(Emitter.Event event) {
        emitter.add(event);
    }


    /**
     * Responsible for sending a specific message to a specific socket
     *
     * @param socket specific socket client
     * @param message message to send
     */
    @Override
    public void send(Socket socket, String message) {
        if (!socketsClients.isEmpty()) {
            int trysConcurrentModificationException = 0;
            while (trysConcurrentModificationException <= 100 && !socketsClients.isEmpty()) {
                try {
                    for (ServerIORunnable client : socketsClients) {
                        if (socket == client.getsocket()) {
                            try {
                                int trys = 0;
                                while (trys <= 100) {
                                    try {
                                        client.getOutPrintWriter().println(message);
                                        break;
                                    } catch (Exception e) {
                                        Thread.sleep(milliseconds);
                                        trys++;
                                    }
                                }
                            } catch (Exception e) {
                                emitter.emitter(Emitter.EVENT_ERROR, e);
                            }
                        }
                    }
                } catch (ConcurrentModificationException e) {
                    emitter.emitter(Emitter.EVENT_ERROR, e);
                    trysConcurrentModificationException++;
                }
            }
        }
    }

    /**
     * Responsible for sending message to all connected clients
     *
     * @param message message to send
     */
    @Override
    public void sendAll(String message) {
        if (!socketsClients.isEmpty()) {
            int trysConcurrentModificationException = 0;
            while (trysConcurrentModificationException <= 100 && !socketsClients.isEmpty()) {
                try {
                    for (ServerIORunnable client : socketsClients) {
                        try {
                            int trys = 0;
                            while (trys <= 100) {
                                try {
                                    client.getOutPrintWriter().println(message);
                                    break;
                                } catch (Exception e) {
                                    Thread.sleep(milliseconds);
                                    trys++;
                                }
                            }
                        } catch (Exception e) {
                            emitter.emitter(Emitter.EVENT_ERROR, e);
                        }
                    }
                } catch (ConcurrentModificationException e) {
                    emitter.emitter(Emitter.EVENT_ERROR, e);
                    trysConcurrentModificationException++;
                }
            }
        }
    }

    /**
     * Responsible for finalizing the execution of the server
     *
     * @return true a indicating whether the server shutdown occurred normally
     */
    @Override
    public boolean close() {
        socketsClients.clear();
        try {
            serverSocket.close();
            if (threadServerSocket != null) {
                threadServerSocket.interrupt();
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Responsible for knowing if the server is running
     *
     * @return true if the server is running
     */
    @Override
    public boolean isRun() {
        return threadServerSocket != null && threadServerSocket.isAlive() && !serverSocket.isClosed();
    }

    /**
     * Returns the number of connected clients
     *
     * @return the value of the number of clients that are connected
     */
    @Override
    public int sizeClients() {
        return this.socketsClients.size();
    }

    private synchronized void removeSocketClient(Socket socket) {
        if (socket != null) {
            int trysConcurrentModificationException = 0;
            while (trysConcurrentModificationException <= 100 && !socketsClients.isEmpty()) {
                try {
                    for (ServerIORunnable ioRunnable : socketsClients) {
                        if (ioRunnable != null && ioRunnable.getsocket().equals(socket)) {
                            socketsClients.remove(ioRunnable);
                        }
                    }
                } catch (ConcurrentModificationException | NoSuchElementException e) {
                    emitter.emitter(Emitter.EVENT_ERROR, e);
                    trysConcurrentModificationException++;
                }
            }
        }
    }

}
