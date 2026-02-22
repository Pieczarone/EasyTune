package com.easytune.fuelmap.controller;

import com.easytune.fuelmap.model.CalculationRequest;
import com.easytune.fuelmap.model.CalculationResponse;
import com.easytune.fuelmap.service.VeCalculatorService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ve")
@CrossOrigin(origins = "*")
public class VeCalculatorController {
    private final VeCalculatorService veCalculatorService;

    public VeCalculatorController(VeCalculatorService veCalculatorService) {
        this.veCalculatorService = veCalculatorService;
    }

    @PostMapping("/calculate")
    public CalculationResponse calculate(@RequestBody CalculationRequest request) {
        return veCalculatorService.calculate(request);
    }
}
