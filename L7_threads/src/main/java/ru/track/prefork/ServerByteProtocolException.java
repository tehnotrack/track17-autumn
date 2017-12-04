package ru.track.prefork;

public class ServerByteProtocolException extends Exception {

    public ServerByteProtocolException(String message) {
        super(message);
    }

    public ServerByteProtocolException(String message, Exception cause) {
        super(message, cause);
    }

}