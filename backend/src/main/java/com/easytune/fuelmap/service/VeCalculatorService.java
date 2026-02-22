package com.easytune.fuelmap.service;

import com.easytune.fuelmap.exception.ValidationException;
import com.easytune.fuelmap.model.CalculationRequest;
import com.easytune.fuelmap.model.CalculationResponse;
import com.easytune.fuelmap.model.FuelTable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class VeCalculatorService {

    public CalculationResponse calculate(CalculationRequest request) {
        validateRequest(request);
        FuelTable veCurrent = request.getVeCurrent();
        FuelTable afrActual = request.getAfrActual();
        FuelTable afrTarget = request.getAfrTarget();

        int rows = veCurrent.getMapAxis().size();
        int cols = veCurrent.getRpmAxis().size();

        List<List<Double>> correctedValues = new ArrayList<>();
        List<List<Double>> deltaValues = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            List<Double> correctedRow = new ArrayList<>();
            List<Double> deltaRow = new ArrayList<>();

            for (int c = 0; c < cols; c++) {
                double veOld = veCurrent.getValues().get(r).get(c);
                Double actual = afrActual.getValues().get(r).get(c);
                Double target = afrTarget.getValues().get(r).get(c);

                validateCellValues(veOld, actual, target, r, c);

                double veNew = veOld;
                if (target != null && actual != null && target != 0.0 && actual != 0.0) {
                    veNew = veOld * (actual / target);
                }
                veNew = round(veNew);

                double delta = 0.0;
                if (veOld != 0.0) {
                    delta = ((veNew - veOld) / veOld) * 100.0;
                }
                delta = round(delta);

                correctedRow.add(veNew);
                deltaRow.add(delta);
            }

            correctedValues.add(correctedRow);
            deltaValues.add(deltaRow);
        }

        FuelTable corrected = new FuelTable();
        corrected.setRpmAxis(veCurrent.getRpmAxis());
        corrected.setMapAxis(veCurrent.getMapAxis());
        corrected.setValues(correctedValues);

        FuelTable delta = new FuelTable();
        delta.setRpmAxis(veCurrent.getRpmAxis());
        delta.setMapAxis(veCurrent.getMapAxis());
        delta.setValues(deltaValues);

        return new CalculationResponse(corrected, delta);
    }

    private void validateRequest(CalculationRequest request) {
        if (request == null || request.getVeCurrent() == null || request.getAfrActual() == null || request.getAfrTarget() == null) {
            throw new ValidationException("Request and all tables are required.");
        }

        FuelTable ve = request.getVeCurrent();
        FuelTable actual = request.getAfrActual();
        FuelTable target = request.getAfrTarget();

        validateTableStructure("VE current", ve);
        validateTableStructure("AFR actual", actual);
        validateTableStructure("AFR target", target);

        if (!Objects.equals(ve.getRpmAxis(), actual.getRpmAxis()) || !Objects.equals(ve.getRpmAxis(), target.getRpmAxis())
                || !Objects.equals(ve.getMapAxis(), actual.getMapAxis()) || !Objects.equals(ve.getMapAxis(), target.getMapAxis())) {
            throw new ValidationException("Axis mismatch between tables. All RPM and MAP axes must match.");
        }
    }

    private void validateTableStructure(String name, FuelTable table) {
        if (table.getRpmAxis() == null || table.getMapAxis() == null || table.getValues() == null) {
            throw new ValidationException(name + " axis and values are required.");
        }

        int rows = table.getMapAxis().size();
        int cols = table.getRpmAxis().size();
        if (table.getValues().size() != rows) {
            throw new ValidationException(name + " row count must equal MAP axis size.");
        }

        for (int r = 0; r < rows; r++) {
            List<Double> row = table.getValues().get(r);
            if (row == null || row.size() != cols) {
                throw new ValidationException(name + " column count must equal RPM axis size for each row.");
            }
        }
    }

    private void validateCellValues(double veOld, Double actual, Double target, int row, int col) {
        if (Double.isNaN(veOld) || veOld < 0) {
            throw new ValidationException("Invalid VE at row " + row + ", col " + col + ".");
        }

        if (actual != null && (Double.isNaN(actual) || actual < 8 || actual > 20)) {
            throw new ValidationException("AFR actual out of range (8-20) at row " + row + ", col " + col + ".");
        }

        if (target != null && (Double.isNaN(target) || target < 8 || target > 20)) {
            throw new ValidationException("AFR target out of range (8-20) at row " + row + ", col " + col + ".");
        }
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
