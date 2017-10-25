package ru.track.beans;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 */
public class Engine implements Serializable {
    private int power;

    public Engine() {
    }

    public Engine(int power) {
        this.power = power;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Engine engine = (Engine) o;
        return power == engine.power;
    }

    @Override
    public int hashCode() {
        return Objects.hash(power);
    }

    @Override
    public String toString() {
        return "Engine{" +
                "power=" + power +
                '}';
    }
}
