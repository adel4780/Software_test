package fr.univnantes.trainreservation.impl;

import fr.univnantes.trainreservation.City;

public class CityImpl implements City {

    private final String name;

    public CityImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
