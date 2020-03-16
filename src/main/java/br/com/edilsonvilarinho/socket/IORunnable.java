package br.com.edilsonvilarinho.socket;


import java.io.*;
import java.net.Socket;

/**
 * ServerIORunnable is responsible for listening for a message connection between the
 * sockets
 *
 * @author edilson.souza
 */
public class IORunnable implements Runnable {

    private Socket socket;
    private Thread thread;
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;
    private PrintWriter outPrintWriter;
    private BufferedReader in;

    private Emitter emitter = null;

    public IORunnable() {
    }

    public IORunnable(Socket socket) {
        this.socket = socket;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public IORunnable(Socket socket, Emitter emitter) {
        this.socket = socket;
        this.thread = new Thread(this);
        this.emitter = emitter;
        this.thread.start();
    }

    public IORunnable(Socket socketClient,
                      String nameThread) {
        this.socket = socketClient;
        this.thread = new Thread(this, nameThread);
        this.thread.start();
    }

    public IORunnable(Socket socket, Emitter emitter,
                      String nameThread) {
        this.socket = socket;
        this.emitter = emitter;
        this.thread = new Thread(this, nameThread);
        this.thread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
//                if (objectOutputStream == null) {
//                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//                }
//                if (objectInputStream == null) {
//                    objectInputStream = new ObjectInputStream(socket.getInputStream());
//
//                }
//                Object object = objectInputStream.readObject();

                outPrintWriter = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String inputLine = "";
                while ((inputLine = in.readLine()) != null) {
                    //String execute = acao.execute(socket.getInetAddress().getHostAddress(), inputLine);
                    //out.println(execute);
                    outPrintWriter.println("OK");
                    
                    if (emitter != null) {
                        emitter.emitter(Emitter.EVENT_MESSAGE_CURRENT_SOCKET_CLIENT, socket.getInetAddress().getHostAddress(), inputLine, socket);
                        emitter.emitter(Emitter.EVENT_MESSAGE, socket.getInetAddress().getHostAddress(), inputLine);
                    }
                }
                if (inputLine == null) {
                    throw new IOException();
                }

//                if (emitter != null) {
//                    emitter.emitter(Emitter.EVENT_MESSAGE_CURRENT_SOCKET_CLIENT, socket.getInetAddress().getHostAddress(), inputLine, socket);
//                    emitter.emitter(Emitter.EVENT_MESSAGE, socket.getInetAddress().getHostAddress(), inputLine);
//                }
            } catch (StreamCorruptedException e
//                    | ClassNotFoundException e
            ) {
                if (emitter != null) {
                    emitter.emitter(Emitter.EVENT_ERROR, "Class name : " + e.getClass().getName() + "\n Mensagem" + e.getMessage());
                }
                break;
            } catch (IOException e) {
                if (emitter != null) {
                    emitter.emitter(Emitter.EVENT_CLIENT_DISCONNECT, socket);
                    emitter.emitter(Emitter.EVENT_SERVER_DISCONNECT, socket);
                }
                thread.interrupt();
                break;
            } catch (Exception e) {
                if (emitter != null) {
                    emitter.emitter(Emitter.EVENT_ERROR, "Class name : " + e.getClass().getName() + "\n Mensagem" + e.getMessage());
                }
                thread.interrupt();
                break;
            }
        }
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public PrintWriter getOutPrintWriter() {
        return outPrintWriter;
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getsocket() {
        return this.socket;
    }
}
