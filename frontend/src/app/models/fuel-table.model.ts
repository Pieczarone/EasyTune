export interface FuelTable {
  rpmAxis: number[];
  mapAxis: number[];
  values: number[][];
}

export interface CalculationRequest {
  veCurrent: FuelTable;
  afrActual: FuelTable;
  afrTarget: FuelTable;
}

export interface CalculationResponse {
  veCorrected: FuelTable;
  deltaPercent: FuelTable;
}
