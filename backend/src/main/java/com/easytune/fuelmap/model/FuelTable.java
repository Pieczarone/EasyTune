package com.easytune.fuelmap.model;

import java.util.List;

public class FuelTable {
    private List<Double> rpmAxis;
    private List<Double> mapAxis;
    private List<List<Double>> values;

    public List<Double> getRpmAxis() {
        return rpmAxis;
    }

    public void setRpmAxis(List<Double> rpmAxis) {
        this.rpmAxis = rpmAxis;
    }

    public List<Double> getMapAxis() {
        return mapAxis;
    }

    public void setMapAxis(List<Double> mapAxis) {
        this.mapAxis = mapAxis;
    }

    public List<List<Double>> getValues() {
        return values;
    }

    public void setValues(List<List<Double>> values) {
        this.values = values;
    }
}
