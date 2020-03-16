package br.com.edilsonvilarinho.socket.server;


import br.com.edilsonvilarinho.socket.Emitter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.fail;

public class ServerImplTest {

    final String SERVER_SEND_MESSAGE = "Server : Hi";
    final String CLIENT_SEND_MESSAGE = "Client : Hi";
    final String SERVER_IP_LOCALHOST = "127.0.0.1";
    final Integer SERVER_PORT = 8003;
    Server serverPrintStream;

    @Before
    public void setUp() {
        try {
            serverPrintStream = new ServerImpl(SERVER_PORT);
            serverPrintStream.start();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @After
    public void tearDown() {
        serverPrintStream.close();
    }

    @Test
    public void serverShouldSendMessageToClient() {
        try {
            serverPrintStream.addEvents(new Emitter.Event(Emitter.EVENT_OPEN, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Socket socket = (Socket) args[0];
                    serverPrintStream.send(socket, SERVER_SEND_MESSAGE);
                }
            }));
            Assert.assertTrue(createSocketWaitMessage().equalsIgnoreCase(SERVER_SEND_MESSAGE));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void serverShouldSendMessageToAllClients() {
        serverPrintStream.addEvents(new Emitter.Event(Emitter.EVENT_OPEN, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (serverPrintStream.sizeClients() == 2) {
                    serverPrintStream.sendAll(SERVER_SEND_MESSAGE);
                }
            }
        }));
        final AtomicReference<String> messageClientOne = new AtomicReference<>("");
        Thread clientOne = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    messageClientOne.set(ServerImplTest.this.createSocketWaitMessage());
                } catch (IOException e) {
                    fail(e.getMessage());
                }
            }
        });
        clientOne.start();

        final AtomicReference<String> messageClientTwo = new AtomicReference<>("");
        Thread clientTwo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    messageClientTwo.set(ServerImplTest.this.createSocketWaitMessage());
                } catch (IOException e) {
                    fail(e.getMessage());
                }
            }
        });
        clientTwo.start();

        boolean stop;
        do {
            stop = !clientOne.isAlive() && !clientTwo.isAlive();
        } while (!stop);

        Assert.assertTrue(messageClientOne.get().equalsIgnoreCase(SERVER_SEND_MESSAGE) && messageClientTwo.get().equalsIgnoreCase(SERVER_SEND_MESSAGE));
    }

    @Test
    public void serverMustBeRunning() {
        Assert.assertTrue(serverPrintStream.isRun());
    }

    @Test
    public void serverMustBeClosed() {
        serverPrintStream.close();
        Assert.assertFalse(serverPrintStream.isRun());
    }

    @Test
    public void serverShouldReceiveAConnectionOpeningEvent() {
        Emitter.Event eventOpen = new Emitter.Event(Emitter.EVENT_OPEN, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Assert.assertTrue(args.length > 0);
            }
        });
        serverPrintStream.addEvents(eventOpen);
        try {
            createSocket();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void serverShouldReceiveClientDisconnectEvent() {
        final AtomicBoolean activeEvent = new AtomicBoolean(false);
        Emitter.Event eventOpen = new Emitter.Event(Emitter.EVENT_CLIENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                activeEvent.set(true);
            }
        });
        serverPrintStream.addEvents(eventOpen);
        try {
            createSocket();
        } catch (IOException e) {
            fail(e.getMessage());
        }
        boolean stop;
        do {
            stop = activeEvent.get();
        } while (!stop);
        Assert.assertTrue(activeEvent.get());
    }

    @Test
    public void serverShouldReceiveClientMessageEvent() {
        Emitter.Event eventOpen = new Emitter.Event(Emitter.EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Assert.assertTrue(args.length > 0);
            }
        });
        serverPrintStream.addEvents(eventOpen);
        try {
            createSocketSendMessage();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    public void createSocket() throws IOException {
        Socket client;
        client = new Socket(SERVER_IP_LOCALHOST, SERVER_PORT);
        client.close();
    }

    String createSocketWaitMessage() throws IOException {
        Socket client = new Socket(SERVER_IP_LOCALHOST, SERVER_PORT);
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String m = in.readLine();
        in.close();
        out.close();
        client.close();
        return m;
    }

    public void createSocketSendMessage() throws IOException {
        Socket client = new Socket(SERVER_IP_LOCALHOST, SERVER_PORT);
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out.println(CLIENT_SEND_MESSAGE);
        in.close();
        out.close();
        client.close();
    }
}