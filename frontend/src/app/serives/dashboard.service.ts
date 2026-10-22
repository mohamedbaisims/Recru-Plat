import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardStats {
  totalCandidatures: number;
  totalCandidats: number;
  totalManagers: number;
  totalWorkDomains: number;
  acceptedCandidatures: number;
  rejectedCandidatures: number;
  pendingCandidatures: number;
  candidaturesByDomain: { [key: string]: number };
  candidaturesByStatus: { [key: string]: number };
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/api/v1/admin/dashboard';

  constructor(private http: HttpClient) { }

  getStatistics(): Observable<DashboardStats> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<DashboardStats>(`${this.apiUrl}/statistics`, { headers });
  }
}
