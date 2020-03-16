package br.com.edilsonvilarinho.socket.server;


import br.com.edilsonvilarinho.socket.Emitter;

import java.io.*;
import java.net.Socket;

/**
 * ServerIORunnable is responsible for listening for a message connection between the
 * sockets
 *
 * @author edilson.souza
 */
public class ServerIORunnable implements Runnable {

    private Socket socket;
    private Thread thread;
    private PrintWriter outPrintWriter;
    private Emitter emitter;

    public ServerIORunnable(Socket socket, Emitter emitter, String nameThread) {
        this.socket = socket;
        this.thread = new Thread(this, nameThread);
        this.emitter = emitter;
        this.thread.start();
    }

    private void printStream() throws IOException {
        outPrintWriter = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            emitter.emitter(Emitter.EVENT_MESSAGE_CURRENT_SOCKET_CLIENT, socket, inputLine);
            emitter.emitter(Emitter.EVENT_MESSAGE, socket, inputLine);
        }
        throw new IOException("printStream closed");
    }

    @Override
    public void run() {
        while (true) {
            try {
                printStream();
            } catch (StreamCorruptedException e) {
                emitter.emitter(Emitter.EVENT_ERROR, e);
                stop();
                break;
            } catch (IOException e) {
                emitter.emitter(Emitter.EVENT_CLIENT_DISCONNECT, socket);
                emitter.emitter(Emitter.EVENT_SERVER_DISCONNECT, socket);
                stop();
                break;
            } catch (Exception e) {
                emitter.emitter(Emitter.EVENT_ERROR, e);
                stop();
                break;
            }
        }
    }

    public PrintWriter getOutPrintWriter() {
        return outPrintWriter;
    }

    public void stop() {
        try {
            if (thread != null) {
                thread.interrupt();
            }
            if (socket != null) {
                if (outPrintWriter != null) {
                    outPrintWriter.close();
                }
                socket.close();
            }
        } catch (Exception e) {
            emitter.emitter(Emitter.EVENT_ERROR, e);
        }
    }

    public Socket getsocket() {
        return this.socket;
    }

}
