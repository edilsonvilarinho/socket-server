package br.com.edilsonvilarinho.socket;

import java.util.ArrayList;

/**
 * @author edilson.souza
 */
public class Emitter {

    /**
     * Called on successful connection.
     */
    public static final String EVENT_OPEN = "open";

    /**
     * Called on disconnection.
     */
    public static final String EVENT_CLOSE = "close";

    /**
     * Called when data is received from the server.
     */
    public static final String EVENT_MESSAGE = "message";

    /**
     * Called when an error occurs.
     */
    public static final String EVENT_ERROR = "error";

    /**
     * Called when an error server disconnect.
     */
    public static final String EVENT_SERVER_DISCONNECT = "server disconnect";

    /**
     * Called when an error client disconnect.
     */
    public static final String EVENT_CLIENT_DISCONNECT = "client disconnect";

    /**
     * Called when data is received message corrent from socket client.
     */
    public static final String EVENT_MESSAGE_CURRENT_SOCKET_CLIENT = "message corrent from socket client";

    private final ArrayList<Event> events = new ArrayList<>();

    public Emitter add(String name, Listener listener) {
        events.add(new Event(name, listener));
        return this;
    }

    public void add(Event event) {
        events.add(event);
    }

    public int sizeEvents() {
        return events.size();
    }

    public Emitter emitter(String nameEvent, Object... args) {
        if (nameEvent != null && !nameEvent.trim().isEmpty()) {
            for (Event event : events) {
                if (event.getName().equalsIgnoreCase(nameEvent)) {
                    event.getListener().call(args);
                }
            }
        }
        return this;
    }

    public interface Listener {

        void call(Object... args);
    }

    public static class Event {
        private final String name;
        private final Listener listener;

        public Event(String name, Listener listener) {
            this.name = name;
            this.listener = listener;
        }

        public String getName() {
            return name;
        }

        public Listener getListener() {
            return listener;
        }
    }
}
