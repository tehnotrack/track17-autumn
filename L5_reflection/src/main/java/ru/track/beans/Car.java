package ru.track.beans;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 */
public class Car implements Serializable {



    private long id;
    private String model;

    private Engine engine;

    private String brand;

    public Car() {
    }

    public Car(long id, String model, Engine engine) {
        this.id = id;
        this.model = model;
        this.engine = engine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return id == car.id &&
                Objects.equals(model, car.model) &&
                Objects.equals(engine, car.engine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, engine);
    }

//    @Override
//    public String toString() {
//        return "Car hello!{" +
//                "id=" + id +
//                ", model='" + model + '\'' +
//                ", engine=" + engine +
//                '}';
//    }

    public static void main(String[] args) {
        Car car = new Car(1L, null, null);
        System.out.println(car);
    }
}
