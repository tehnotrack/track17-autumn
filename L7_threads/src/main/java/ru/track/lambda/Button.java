package ru.track.lambda;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Button {
    List<Lambda.Listener> listeners = new ArrayList();

    public void addListener(Lambda.Listener listener) {
        listeners.add(listener);
    }

    public void press() {

        for (Lambda.Listener listener : listeners) {
            listener.onAction("");
        }
//        listeners.forEach((l) -> l.onAction());
    }
}
