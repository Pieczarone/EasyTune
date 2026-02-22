import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CalculationRequest, CalculationResponse } from '../models/fuel-table.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class VeApiService {
  private readonly apiUrl = 'http://localhost:8080/api/ve/calculate';

  constructor(private http: HttpClient) {}

  calculate(request: CalculationRequest): Observable<CalculationResponse> {
    return this.http.post<CalculationResponse>(this.apiUrl, request);
  }
}
