import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApplicationForm } from '../models/application-form';
import { ApplicationFormDTO } from '../models/application-form-dto';
@Injectable({ providedIn: 'root' })
export class ApplicationFormService {
  private baseUrl = 'http://localhost:8080/api/v1/applications';

  constructor(private http: HttpClient) {}

 create(form: ApplicationForm): Observable<ApplicationForm> {
   const token = localStorage.getItem('token');
   const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
   return this.http.post<ApplicationForm>(this.baseUrl, form, { headers });
 }

  getByUser(userId: number): Observable<ApplicationForm[]> {
    return this.http.get<ApplicationForm[]>(`${this.baseUrl}/user/${userId}`);
  }

  update(id: number, form: ApplicationForm): Observable<ApplicationForm> {
    return this.http.put<ApplicationForm>(`${this.baseUrl}/${id}`, form);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
createFormWithFiles(formData: FormData) {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });

  return this.http.post('http://localhost:8080/api/v1/applications/with-files', formData, { headers });
}

extractCv(formData: FormData): Observable<any> {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  return this.http.post<any>('http://localhost:8080/api/v1/applications/extract-cv', formData, { headers });
}
getAllApplications(): Observable<ApplicationFormDTO[]> {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

  return this.http.get<ApplicationFormDTO[]>(
    'http://localhost:8080/api/v1/applications',
    { headers }
  );
}
getApplicationsByUser(userId: number): Observable<ApplicationFormDTO[]> {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
  return this.http.get<ApplicationFormDTO[]>(`http://localhost:8080/api/v1/applications/user/${userId}`, { headers });
}

deleteApplication(id: number): Observable<void> {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
  return this.http.delete<void>(`http://localhost:8080/api/v1/applications/${id}`, { headers });
}
getUnassignedApplications(): Observable<ApplicationFormDTO[]> {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });

  return this.http.get<ApplicationFormDTO[]>(
    'http://localhost:8080/api/v1/applications/unassigned',
    { headers }
  );
}
getAssignedApplicationsCount(): Observable<number> {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

  return this.http.get<number>(
    'http://localhost:8080/api/v1/applications/assigned/count',
    { headers }
  );
}

getFile(applicationId: number, type: string): Observable<Blob> {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

  return this.http.get(`${this.baseUrl}/${applicationId}/file/${type}`, {
    headers,
    responseType: 'blob' // ✅ sans "as 'json'"
  });
}
getDashboardStats() {
  const token = localStorage.getItem('token');

  return this.http.get<any>(
    'http://localhost:8080/api/v1/applications/dashboard',
    {
      headers: { Authorization: `Bearer ${token}` }
    }
  );
}
}
