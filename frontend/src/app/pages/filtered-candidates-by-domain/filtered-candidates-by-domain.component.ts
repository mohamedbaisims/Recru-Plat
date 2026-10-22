import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ApplicationActionsService } from '../../serives/application-actions.service';

interface WorkDomain {
  id: number;
  name: string;
}

interface ApplicationFormDTO {
  id: number;
  fullName: string;
  address: string;
  email: string;
  phoneNumber?: string;
  diploma: string;
  experience: string;
  technicalSkills: string;
  personalSkills: string;
  workDomain?: WorkDomain;
  status?: string; // 🆕 Application status (PENDING, INTERVIEW_SCHEDULED, REJECTED)
}

interface InterviewRequest {
  interviewDate: string;
  interviewTime: string;
  interviewType: string;
  meetingLink?: string;
  notes?: string;
}

@Component({
  selector: 'app-filtered-candidates-by-domain',
  templateUrl: './filtered-candidates-by-domain.component.html',
  styleUrls: ['./filtered-candidates-by-domain.component.scss']
})
export class FilteredCandidatesByDomainComponent implements OnInit {
  workDomains: WorkDomain[] = [];
  filteredDomains: WorkDomain[] = [];
  selectedDomainId: number | null = null;
  selectedDomainName: string | null = null;
  applications: ApplicationFormDTO[] = [];
  isOpen = false;
  searchTerm = '';
  isLoading = false;

  // 🆕 Modals et actions
  showRejectModal = false;
  showInterviewModal = false;
  selectedApplication: ApplicationFormDTO | null = null;

  // 🆕 Données du formulaire de rejet
  rejectionReason = '';

  // 🆕 Données du formulaire d'entretien
  interviewForm: InterviewRequest = {
    interviewDate: '',
    interviewTime: '',
    interviewType: 'Technique',
    meetingLink: '',
    notes: ''
  };

  // 🆕 Flags pour les states de soumission
  isSubmittingReject = false;
  isSubmittingInterview = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private http: HttpClient,
    private applicationActionsService: ApplicationActionsService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadDomains();
  }

  loadDomains(): void {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.get<WorkDomain[]>('http://localhost:8080/api/v1/work-domains', { headers }).subscribe({
      next: (data) => {
        this.workDomains = data;
        this.filteredDomains = data;
      },
      error: (err) => console.error('Erreur chargement domaines', err)
    });
  }

  toggleDropdown(): void {
    this.isOpen = !this.isOpen;
    if (this.isOpen) {
      this.searchTerm = '';
      this.filteredDomains = this.workDomains;
    }
  }

  selectDomain(domain: WorkDomain): void {
    this.selectedDomainId = domain.id;
    this.selectedDomainName = domain.name;
    this.isOpen = false;
    this.searchTerm = '';
    this.loadApplications();
  }

  filterDomains(): void {
    if (!this.searchTerm.trim()) {
      this.filteredDomains = this.workDomains;
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredDomains = this.workDomains.filter(domain =>
        domain.name.toLowerCase().includes(term) ||
        domain.id.toString().includes(term)
      );
    }
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.filteredDomains = this.workDomains;
  }

  loadApplications(): void {
    if (!this.selectedDomainId) return;

    this.isLoading = true;
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.get<ApplicationFormDTO[]>(
      `http://localhost:8080/api/v1/applications/by-domain/${this.selectedDomainId}`,
      { headers }
    ).subscribe({
      next: (data) => {
        this.applications = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erreur chargement candidatures', err);
        this.isLoading = false;
        this.applications = [];
      }
    });
  }

  getSkillsArray(skills: string): string[] {
    if (!skills) return [];
    const cleanSkills = skills.replace(/[\[\]"]/g, '');
    return cleanSkills.split(',')
      .map(skill => skill.trim())
      .filter(skill => skill.length > 0);
  }

  // 🆕 ACTIONS DU MANAGER

  openRejectModal(application: ApplicationFormDTO): void {
    this.selectedApplication = application;
    this.rejectionReason = '';
    this.showRejectModal = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();
  }

  closeRejectModal(): void {
    this.showRejectModal = false;
    this.selectedApplication = null;
    this.rejectionReason = '';
    this.cdr.detectChanges();
  }

  submitReject(): void {
    if (!this.selectedApplication || !this.rejectionReason.trim()) {
      this.errorMessage = 'Veuillez entrer une raison de refus';
      this.cdr.detectChanges();
      return;
    }

    this.isSubmittingReject = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    this.applicationActionsService.rejectApplication(
      this.selectedApplication.id,
      this.rejectionReason
    ).subscribe({
      next: (response) => {
        this.successMessage = 'Candidature refusée et notifiée';
        this.isSubmittingReject = false;
        this.cdr.detectChanges();
        setTimeout(() => {
          this.closeRejectModal();
          this.loadApplications();
        }, 1500);
      },
      error: (err) => {
        this.errorMessage = 'Erreur lors du refus: ' + (err.error?.message || 'Erreur inconnue');
        this.isSubmittingReject = false;
        this.cdr.detectChanges();
      }
    });
  }

  openInterviewModal(application: ApplicationFormDTO): void {
    this.selectedApplication = application;
    this.interviewForm = {
      interviewDate: '',
      interviewTime: '',
      interviewType: 'Technique',
      meetingLink: '',
      notes: ''
    };
    this.showInterviewModal = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();
  }

  closeInterviewModal(): void {
    this.showInterviewModal = false;
    this.selectedApplication = null;
    this.cdr.detectChanges();
  }

  submitInterview(): void {
    if (!this.selectedApplication) return;

    if (!this.interviewForm.interviewDate || !this.interviewForm.interviewTime) {
      this.errorMessage = 'Veuillez entrer la date et l\'heure de l\'entretien';
      this.cdr.detectChanges();
      return;
    }

    this.isSubmittingInterview = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    this.applicationActionsService.scheduleInterview(
      this.selectedApplication.id,
      this.interviewForm.interviewDate,
      this.interviewForm.interviewTime,
      this.interviewForm.interviewType,
      this.interviewForm.meetingLink
    ).subscribe({
      next: (response) => {
        this.successMessage = 'Entretien planifié et candidat notifié';
        this.isSubmittingInterview = false;
        this.cdr.detectChanges();
        setTimeout(() => {
          this.closeInterviewModal();
          this.loadApplications();
        }, 1500);
      },
      error: (err) => {
        this.errorMessage = 'Erreur lors de la planification: ' + (err.error?.message || 'Erreur inconnue');
        this.isSubmittingInterview = false;
        this.cdr.detectChanges();
      }
    });
  }
}
