import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApplicationActionsService {
  private apiUrl = 'http://localhost:8080/api/v1/applications';

  constructor(private http: HttpClient) {}

  /**
   * Récupère le token JWT du localStorage
   */
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  /**
   * Refuser une candidature
   * @param applicationId ID de la candidature
   * @param reason Raison du refus
   */
  rejectApplication(applicationId: number, reason: string): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/${applicationId}/reject`,
      { reason },
      { headers: this.getHeaders() }
    );
  }

  /**
   * Planifier un entretien
   * @param applicationId ID de la candidature
   * @param interviewDate Date de l'entretien (YYYY-MM-DD)
   * @param interviewTime Heure de l'entretien (HH:mm)
   * @param interviewType Type d'entretien (ex: "Technique", "RH")
   * @param meetingLink Lien de la réunion (optionnel)
   */
  scheduleInterview(
    applicationId: number,
    interviewDate: string,
    interviewTime: string,
    interviewType: string,
    meetingLink?: string
  ): Observable<any> {
    const body = {
      interviewDate,
      interviewTime,
      interviewType,
      meetingLink: meetingLink || ''
    };

    return this.http.put(
      `${this.apiUrl}/${applicationId}/schedule-interview`,
      body,
      { headers: this.getHeaders() }
    );
  }
}
