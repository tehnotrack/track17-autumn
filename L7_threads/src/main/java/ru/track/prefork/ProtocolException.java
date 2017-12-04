package ru.track.prefork;

public class ProtocolException extends Exception {

    ProtocolException(String message) {
        super(message);
    }

    ProtocolException(String message, Exception cause) {
        super(message, cause);
    }
}
