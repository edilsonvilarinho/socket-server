# Socket-Server

This is the Socket Server Library for Java, which is simply ported from the [ServerSocket](https://docs.oracle.com/javase/8/docs/api/java/net/ServerSocket.html).

## Installation

The latest artifact is available on Maven Central. You'll also need to install.

### Maven

Add the following dependency to your `pom.xml`.

```xml
<dependencies>
  <dependency>
    <groupId>br.com.edilsonvilarinho</groupId>
    <artifactId>socket-server</artifactId>
    <version>1.0.1</version>
  </dependency>
</dependencies>
```

### Gradle

Add it as a gradle dependency for Android Studio, in `build.gradle`:

```groovy
implementation 'br.com.edilsonvilarinho:socket-server:1.0.1'
```

## Usage

You use `Server` to initialize `Socket`:

```java
Integer SERVER_PORT = 8003;
  try{
    Server server = new ServerImpl(SERVER_PORT);
    server.start();
    server.close();
  }catch(Exception e){
    e.fillInStackTrace();
  }
```
Listening events for actions that can be intercepted by the server.

EVENT_OPEN for this event, the client object that has just been connected is sent to it as a parameter args[0].

```java
/**
* Called on successful connection.
*/
Emitter.Event eventOpen = new Emitter.Event(Emitter.EVENT_OPEN, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                /*...args[0] object socket....*/
            }
        });
server.addEvents(eventOpen);

```

EVENT_MESSAGE for this event, the client object that has just been connected to args [0] and the message that was received args [1] is sent as parameter.

```java
/**
* Called when data is received from the server.
*/
Emitter.Event eventMessage = new Emitter.Event(Emitter.EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                /*...args[0] object socket  args[1] string message ....*/
            }
        });
server.addEvents(eventMessage);

```

EVENT_ERROR for this event it is sent to it as a parameter the exception that has just been connected args [0].
Can be used as an exception log while the server is running.

```java
/**
* Called when data is received from the server.
*/
Emitter.Event eventError = new Emitter.Event(Emitter.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                /*...args[0] Exception ....*/
            }
        });
server.addEvents(eventError);

```

EVENT_CLIENT_DISCONNECT for this event the socket object that just disconnected is sent to args [0].

```java
/**
* Called when data is received from the server.
*/
Emitter.Event eventClientDisconnect = new Emitter.Event(Emitter.EVENT_CLIENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                /*...args[0] object socket ....*/
            }
        });
server.addEvents(eventClientDisconnect);

```

It is possible to send a message to clients that are connected.

```java
String SERVER_SEND_MESSAGE = "Server : Hi";
server.sendAll(SERVER_SEND_MESSAGE);
```

It is possible to send a message to a specific customer.

```java
String SERVER_SEND_MESSAGE = "Server : Hi";
server.send(socketClient,SERVER_SEND_MESSAGE);
```

## Features

This library supports all the features that ServerSocket offers, including events, sending only strings per message. It will soon be available for Android.

## License

MIT
