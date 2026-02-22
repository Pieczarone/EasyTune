package com.easytune.fuelmap.service;

import com.easytune.fuelmap.exception.ValidationException;
import com.easytune.fuelmap.model.CalculationRequest;
import com.easytune.fuelmap.model.CalculationResponse;
import com.easytune.fuelmap.model.FuelTable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VeCalculatorServiceTest {

    private final VeCalculatorService service = new VeCalculatorService();

    @Test
    void calculatesCorrectFormula() {
        CalculationResponse response = service.calculate(buildRequest());
        assertEquals(42.86, response.getVeCorrected().getValues().get(0).get(0));
    }

    @Test
    void protectsFromDivisionByZero() {
        CalculationRequest request = buildRequest();
        request.getAfrTarget().getValues().get(0).set(0, 0.0);
        CalculationResponse response = service.calculate(request);
        assertEquals(50.0, response.getVeCorrected().getValues().get(0).get(0));
    }

    @Test
    void throwsOnAxisMismatch() {
        CalculationRequest request = buildRequest();
        request.getAfrActual().setRpmAxis(List.of(1000.0, 2500.0, 3000.0));
        assertThrows(ValidationException.class, () -> service.calculate(request));
    }

    @Test
    void calculatesDeltaPercent() {
        CalculationResponse response = service.calculate(buildRequest());
        assertEquals(-14.28, response.getDeltaPercent().getValues().get(0).get(0));
    }

    private CalculationRequest buildRequest() {
        List<Double> rpm = List.of(1000.0, 2000.0, 3000.0);
        List<Double> map = List.of(40.0, 60.0, 80.0);

        FuelTable ve = buildTable(rpm, map, List.of(
                List.of(50.0, 55.0, 60.0),
                List.of(60.0, 65.0, 70.0),
                List.of(70.0, 75.0, 80.0)
        ));

        FuelTable actual = buildTable(rpm, map, List.of(
                List.of(12.0, 12.5, 13.0),
                List.of(11.5, 12.0, 12.5),
                List.of(11.0, 11.5, 12.0)
        ));

        FuelTable target = buildTable(rpm, map, List.of(
                List.of(14.0, 14.0, 14.0),
                List.of(13.0, 13.0, 13.0),
                List.of(12.5, 12.5, 12.5)
        ));

        CalculationRequest request = new CalculationRequest();
        request.setVeCurrent(ve);
        request.setAfrActual(actual);
        request.setAfrTarget(target);
        return request;
    }

    private FuelTable buildTable(List<Double> rpm, List<Double> map, List<List<Double>> values) {
        FuelTable table = new FuelTable();
        table.setRpmAxis(rpm);
        table.setMapAxis(map);
        table.setValues(values);
        return table;
    }
}
