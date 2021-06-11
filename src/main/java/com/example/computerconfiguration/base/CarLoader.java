package com.example.computerconfiguration.base;

import com.example.computerconfiguration.domain.Configuration;
import com.example.computerconfiguration.domain.car.Car;
import com.example.computerconfiguration.domain.car.CarModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads car configuration base from the example used in
 * Personalized Diagnosis for Over-Constrained Problems
 * Felfernig et al. (2013)
 */
public class CarLoader implements ConfigurationLoader{

    private static CarLoader instance;

    private CarLoader() {
    }

    public static CarLoader getInstance() {
        if (instance == null) {
            instance = new CarLoader();
        }
        return instance;
    }

    public List<? extends Configuration> getConfigurations() {
        List<Car> cars = new ArrayList<>();
        cars.add(new Car(1, 4, 0, 0, 1));
        cars.add(new Car(1, 4, 0, 0, 0));
        cars.add(new Car(4, 10, 1, 1, 1));
        cars.add(new Car(2, 6, 0, 0, 1));
        cars.add(new Car(3, 6, 0, 0, 0));
        cars.add(new Car(4, 10, 0, 1, 1));
        cars.add(new Car(2, 6, 1, 0, 0));
        cars.add(new Car(3, 6, 1, 0, 0));
        return cars;
    }

    public CarModel getDomainClass() {
        return new CarModel();
    }
}
