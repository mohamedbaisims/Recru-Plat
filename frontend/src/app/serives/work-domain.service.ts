import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { WorkDomain } from '../models/work-domain.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WorkDomainService {

  private apiUrl = 'http://localhost:8080/api/v1/work-domains';

  constructor(private http: HttpClient) {}

  getAll(): Observable<WorkDomain[]> {
    return this.http.get<WorkDomain[]>(this.apiUrl);
  }

  create(domain: WorkDomain): Observable<WorkDomain> {
    return this.http.post<WorkDomain>(this.apiUrl, domain);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
