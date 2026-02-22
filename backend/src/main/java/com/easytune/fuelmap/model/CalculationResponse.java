package com.easytune.fuelmap.model;

public class CalculationResponse {
    private FuelTable veCorrected;
    private FuelTable deltaPercent;

    public CalculationResponse(FuelTable veCorrected, FuelTable deltaPercent) {
        this.veCorrected = veCorrected;
        this.deltaPercent = deltaPercent;
    }

    public FuelTable getVeCorrected() {
        return veCorrected;
    }

    public FuelTable getDeltaPercent() {
        return deltaPercent;
    }
}
