import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { ApplicationFormService } from '../../serives/application-form.service';
import { NotificationService, NotificationDTO } from '../../serives/notification.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Subject } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { takeUntil } from 'rxjs/operators';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, OnDestroy {
  @Input() toggleSidebar!: () => void;

  fullName: string = '';
  notificationCount: number = 0;
  notifications: NotificationDTO[] = [];
  showNotificationDropdown: boolean = false;
  isManager: boolean = false;

  private destroy$ = new Subject<void>();

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadUserInfo();
    this.determineUserRole();
    this.loadNotifications();
    this.subscribeToNotificationUpdates();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // Récupère prénom + nom à partir du token
  private loadUserInfo(): void {
    const token = localStorage.getItem('token');
    if (token) {
      const payload: any = JSON.parse(atob(token.split('.')[1]));
      this.fullName = payload.firstname + ' ' + payload.lastname;
    }
  }

  // 🆕 Déterminer si l'utilisateur est un manager ou un candidat
  private determineUserRole(): void {
    const token = localStorage.getItem('token');
    if (token) {
      const payload: any = JSON.parse(atob(token.split('.')[1]));
      this.isManager = payload.role && payload.role.includes('MANAGER');
    }
  }

  /**
   * Charger les dernières notifications
   */
  private loadNotifications(): void {
    // Charger les notifications en fonction du rôle
    if (this.isManager) {
      this.notificationService.getRecentNotifications()
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (notifications) => {
            this.notifications = notifications;
            this.updateNotificationCount();
          },
          error: (err) => {
            console.error('Erreur chargement notifications :', err);
            this.notificationCount = 0;
          }
        });
    } else {
      // Pour les candidats
      this.notificationService.getRecentNotificationsForCandidate()
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (notifications) => {
            this.notifications = notifications;
            this.updateNotificationCount();
          },
          error: (err) => {
            console.error('Erreur chargement notifications candidat :', err);
            this.notificationCount = 0;
          }
        });
    }
  }

  /**
   * S'abonner aux mises à jour de notifications (polling)
   */
  private subscribeToNotificationUpdates(): void {
    // Souscrire aux mises à jour en fonction du rôle
    if (this.isManager) {
      this.notificationService.getUnreadCountWithPolling()
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (count) => this.notificationCount = count,
          error: (err) => console.error('Erreur polling notifications :', err)
        });
    } else {
      // Pour les candidats
      this.notificationService.getUnreadCountForCandidateWithPolling()
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (count) => this.notificationCount = count,
          error: (err) => console.error('Erreur polling notifications candidat :', err)
        });
    }
  }

  /**
   * Mettre à jour le compte de notifications non lues
   */
  private updateNotificationCount(): void {
    const unreadCount = this.notifications.filter(n => !n.isRead).length;
    this.notificationCount = unreadCount;
  }

  /**
   * Basculer le dropdown des notifications
   */
  toggleNotificationDropdown(): void {
    this.showNotificationDropdown = !this.showNotificationDropdown;
    if (this.showNotificationDropdown) {
      this.loadNotifications();
    }
  }

  /**
   * Marquer une notification comme lue
   */
  markAsRead(notification: NotificationDTO): void {
    this.notificationService.markAsReadByRole(notification.id, this.isManager)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          notification.isRead = true;
          this.updateNotificationCount();
        },
        error: (err) => console.error('Erreur marquant notification comme lue :', err)
      });
  }

  /**
   * Supprimer une notification
   */
  deleteNotification(notificationId: number): void {
    this.notificationService.deleteByRole(notificationId, this.isManager).subscribe({
      next: () => {
        this.notifications = this.notifications.filter(n => n.id !== notificationId);
        this.updateNotificationCount();
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Erreur supprimant notification :', err);
      }
    });
  }

  /**
   * 🆕 Marquer une notification comme lue ET la supprimer
   */
  dismissNotification(notification: NotificationDTO, event?: Event): void {
    event?.stopPropagation();

    this.notificationService.markAsReadByRole(notification.id, this.isManager)
      .pipe(
        switchMap(() => this.notificationService.deleteByRole(notification.id, this.isManager)),
        takeUntil(this.destroy$)
      )
      .subscribe({
      next: () => {
        this.notifications = this.notifications.filter(n => n.id !== notification.id);
        this.updateNotificationCount();
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Erreur traitement notification :', err);
      }
    });
  }

  /**
   * Marquer toutes les notifications comme lues
   */
  markAllAsRead(): void {
    this.notificationService.markAllAsReadByRole(this.isManager)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.notifications.forEach(n => n.isRead = true);
          this.updateNotificationCount();
        },
        error: (err) => console.error('Erreur marquant toutes les notifications comme lues :', err)
      });
  }

  logout(): void {
    localStorage.removeItem('token');
    window.location.href = '/login';
  }
}
