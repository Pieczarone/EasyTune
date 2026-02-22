package com.easytune.fuelmap.model;

public class CalculationRequest {
    private FuelTable veCurrent;
    private FuelTable afrActual;
    private FuelTable afrTarget;

    public FuelTable getVeCurrent() {
        return veCurrent;
    }

    public void setVeCurrent(FuelTable veCurrent) {
        this.veCurrent = veCurrent;
    }

    public FuelTable getAfrActual() {
        return afrActual;
    }

    public void setAfrActual(FuelTable afrActual) {
        this.afrActual = afrActual;
    }

    public FuelTable getAfrTarget() {
        return afrTarget;
    }

    public void setAfrTarget(FuelTable afrTarget) {
        this.afrTarget = afrTarget;
    }
}
