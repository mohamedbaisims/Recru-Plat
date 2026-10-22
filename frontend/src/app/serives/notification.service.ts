import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, interval } from 'rxjs';
import { switchMap, startWith } from 'rxjs/operators';

export interface NotificationDTO {
  id: number;
  title: string;
  message: string;
  candidateName: string;
  candidatEmail: string;
  applicationId: number;
  type: string;
  isRead: boolean;
  createdAt: string;
  readAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:8080/api/v1/notifications';

  constructor(private http: HttpClient) {}

  /**
   * Obtenir les 10 dernières notifications
   */
  getRecentNotifications(): Observable<NotificationDTO[]> {
    const token = localStorage.getItem('token');
    if (!token) {
      return new Observable(subscriber => subscriber.complete());
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<NotificationDTO[]>(`${this.apiUrl}/recent`, { headers });
  }

  /**
   * Obtenir toutes les notifications
   */
  getAllNotifications(): Observable<NotificationDTO[]> {
    const token = localStorage.getItem('token');
    if (!token) {
      return new Observable(subscriber => subscriber.complete());
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<NotificationDTO[]>(`${this.apiUrl}/all`, { headers });
  }

  /**
   * Obtenir les notifications non lues
   */
  getUnreadNotifications(): Observable<NotificationDTO[]> {
    const token = localStorage.getItem('token');
    if (!token) {
      return new Observable(subscriber => subscriber.complete());
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<NotificationDTO[]>(`${this.apiUrl}/unread`, { headers });
  }

  /**
   * Obtenir le nombre de notifications non lues
   */
  getUnreadCount(): Observable<number> {
    const token = localStorage.getItem('token');
    if (!token) {
      return new Observable(subscriber => subscriber.next(0));
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<number>(`${this.apiUrl}/unread-count`, { headers });
  }

  /**
   * Obtenir le nombre de notifications non lues avec polling automatique
   * Remet à jour toutes les 30 secondes
   */
  getUnreadCountWithPolling(): Observable<number> {
    return interval(30000).pipe(
      startWith(0),
      switchMap(() => this.getUnreadCount())
    );
  }

  /**
   * Marquer une notification comme lue
   */
  markAsRead(id: number): Observable<void> {
    const token = localStorage.getItem('token');
    if (!token) {
      return new Observable(subscriber => subscriber.complete());
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.put<void>(`${this.apiUrl}/${id}/read`, {}, { headers });
  }

  /**
   * Marquer toutes les notifications comme lues
   */
  markAllAsRead(): Observable<void> {
    const token = localStorage.getItem('token');
    if (!token) {
      return new Observable(subscriber => subscriber.complete());
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.put<void>(`${this.apiUrl}/mark-all-read`, {}, { headers });
  }

  /**
   * Marquer toutes les notifications candidat comme lues
   */
  markAllCandidateNotificationsAsRead(): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/candidate/mark-all-read`, {}, {
      headers: this.getHeaders()
    });
  }

  /**
   * Supprimer une notification
   */
  deleteNotification(id: number): Observable<void> {
    const token = localStorage.getItem('token');
    if (!token) {
      return new Observable(subscriber => subscriber.complete());
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers });
  }

  /**
   * Supprimer toutes les notifications
   */
  deleteAllNotifications(): Observable<void> {
    const token = localStorage.getItem('token');
    if (!token) {
      return new Observable(subscriber => subscriber.complete());
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete<void>(`${this.apiUrl}/all`, { headers });
  }

  // 🆕 ============= MÉTHODES POUR LES CANDIDATS =============

  /**
   * Obtenir les 10 dernières notifications du candidat
   */
  getRecentNotificationsForCandidate(): Observable<NotificationDTO[]> {
    const token = localStorage.getItem('token');
    if (!token) {
      return new Observable(subscriber => subscriber.complete());
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<NotificationDTO[]>(`${this.apiUrl}/candidate/recent`, { headers });
  }

  /**
   * Obtenir toutes les notifications du candidat
   */
  getAllNotificationsForCandidate(): Observable<NotificationDTO[]> {
    const token = localStorage.getItem('token');
    if (!token) {
      return new Observable(subscriber => subscriber.complete());
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<NotificationDTO[]>(`${this.apiUrl}/candidate/all`, { headers });
  }

  /**
   * Obtenir les notifications non lues du candidat
   */
  getUnreadNotificationsForCandidate(): Observable<NotificationDTO[]> {
    const token = localStorage.getItem('token');
    if (!token) {
      return new Observable(subscriber => subscriber.complete());
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<NotificationDTO[]>(`${this.apiUrl}/candidate/unread`, { headers });
  }

  /**
   * Obtenir le nombre de notifications non lues du candidat
   */
  getUnreadCountForCandidate(): Observable<number> {
    const token = localStorage.getItem('token');
    if (!token) {
      return new Observable(subscriber => subscriber.next(0));
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<number>(`${this.apiUrl}/candidate/unread-count`, { headers });
  }

  /**
   * Obtenir le nombre de notifications non lues du candidat avec polling automatique
   * Remet à jour toutes les 30 secondes
   */
  getUnreadCountForCandidateWithPolling(): Observable<number> {
    return interval(30000).pipe(
      startWith(0),
      switchMap(() => this.getUnreadCountForCandidate())
    );
  }

  /**
   * 🆕 Marquer une notification du candidat comme lue
   */
  markCandidateNotificationAsRead(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/candidate/${id}/read`, {}, {
      headers: this.getHeaders()
    });
  }

  /**
   * 🆕 Supprimer une notification du candidat
   */
  deleteCandidateNotification(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/candidate/${id}`, {
      headers: this.getHeaders()
    });
  }

  markAllAsReadByRole(isManager: boolean): Observable<void> {
    return isManager ? this.markAllAsRead() : this.markAllCandidateNotificationsAsRead();
  }

  markAsReadByRole(id: number, isManager: boolean): Observable<void> {
    return isManager ? this.markAsRead(id) : this.markCandidateNotificationAsRead(id);
  }

  deleteByRole(id: number, isManager: boolean): Observable<void> {
    return isManager ? this.deleteNotification(id) : this.deleteCandidateNotification(id);
  }

  getHeaders() {
    const token = localStorage.getItem('token');
    if (!token) {
      return new HttpHeaders();
    }
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }
}
