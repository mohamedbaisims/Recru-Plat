import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { jwtDecode } from 'jwt-decode';
interface ApplicationForm {
  id: number;
  fullName: string;
  address: string;
  phoneNumber?: string;
  email?: string;
  birthDate?: string;
  birthPlace?: string;
  nationality?: string;
  diploma: string;
  diplomaYear?: string;
  experience: string;
  institution: string;
  technicalSkills: string;
  personalSkills: string;
  cvPath?: string;
  diplomaFilePath?: string;
  motivationLetterPath?: string;
  user?: { id: number };
  workDomain?: unknown;
  status?: string;
}

@Component({
  selector: 'app-user-candidatures-component',
  templateUrl: './user-candidatures-component.component.html',
  styleUrls: ['./user-candidatures-component.component.scss']
})
export class UserCandidaturesComponentComponent implements OnInit {
  applications: ApplicationForm[] = [];
  errorMessage: string = '';
  successMessage: string = '';
  userId: number | null = null;
  showEditModal: boolean = false;
  isSaving: boolean = false;
  editingApplication: ApplicationForm | null = null;
  editForm: ApplicationForm = this.createEmptyForm();
  editCvFile: File | null = null;
  editMotivationFile: File | null = null;
  editDiplomaFile: File | null = null;
  nationalities: string[] = [
    'Tunisienne', 'Française', 'Algérienne', 'Marocaine', 'Canadienne', 'Belge',
    'Italienne', 'Espagnole', 'Allemande', 'Américaine', 'Libanaise', 'Égyptienne'
  ];
  diplomas: string[] = [
    'Baccalauréat', 'BTS', 'Licence', 'Maîtrise', 'Master', 'Mastère professionnel',
    'Mastère recherche', 'Doctorat'
  ];
  diplomaYears: string[] = [];

  constructor(private http: HttpClient) {}

  private createEmptyForm(): ApplicationForm {
    return {
      id: 0,
      fullName: '',
      address: '',
      phoneNumber: '',
      email: '',
      birthDate: '',
      birthPlace: '',
      nationality: '',
      diploma: '',
      diplomaYear: '',
      experience: '',
      institution: '',
      technicalSkills: '',
      personalSkills: '',
      cvPath: '',
      diplomaFilePath: '',
      motivationLetterPath: '',
      user: undefined,
      workDomain: undefined,
      status: undefined
    };
  }

  ngOnInit(): void {
    const currentYear = new Date().getFullYear();
    for (let year = currentYear; year >= 1980; year--) {
      this.diplomaYears.push(year.toString());
    }

    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = '🔒 Token manquant. Veuillez vous reconnecter.';
      return;
    }

    try {
      const decoded: any = jwtDecode(token);
      const userId = decoded?.id;

      if (!userId) {
        this.errorMessage = "❌ L'identifiant utilisateur est introuvable dans le token.";
        return;
      }

      this.userId = userId;
      this.loadApplications(userId);
    } catch (error) {
      console.error("Erreur de décodage du token :", error);
      this.errorMessage = "❌ Token invalide.";
    }
  }

  loadApplications(userId: number) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.get<ApplicationForm[]>(`http://localhost:8080/api/v1/applications/user/${userId}`, { headers }).subscribe({
      next: (data) => {
        console.log("📥 Candidatures reçues :", data);
        this.applications = data;
        this.errorMessage = '';
      },
      error: (err) => {
        console.error("Erreur lors du chargement des candidatures", err);
        this.errorMessage = "Erreur lors du chargement des candidatures.";
      }
    });
  }

  deleteApplication(id: number) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    this.http.delete(`http://localhost:8080/api/v1/applications/${id}`, { headers }).subscribe({
      next: () => {
        this.applications = this.applications.filter(app => app.id !== id);
        this.successMessage = 'Candidature supprimée avec succès.';
        this.errorMessage = '';
        console.log(`🗑️ Candidature ${id} supprimée`);
      },
      error: (err) => {
        console.error("Erreur lors de la suppression", err);
        this.errorMessage = "Erreur lors de la suppression de la candidature.";
      }
    });
  }

  editApplication(app: ApplicationForm) {
    this.successMessage = '';
    this.errorMessage = '';
    this.editingApplication = app;
    this.editForm = {
      ...this.createEmptyForm(),
      ...app,
      user: app.user || (this.userId ? { id: this.userId } : undefined)
    };
    this.showEditModal = true;
  }

  onEditFileSelected(event: Event, type: 'cv' | 'motivation' | 'diploma') {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;

    if (file && file.type !== 'application/pdf' && !file.name.toLowerCase().endsWith('.pdf')) {
      this.errorMessage = 'Seuls les fichiers PDF sont autorisés.';
      input.value = '';
      return;
    }

    this.errorMessage = '';

    if (type === 'cv') {
      this.editCvFile = file;
    } else if (type === 'motivation') {
      this.editMotivationFile = file;
    } else {
      this.editDiplomaFile = file;
    }
  }

  getSelectedFileName(type: 'cv' | 'motivation' | 'diploma'): string {
    if (type === 'cv') {
      return this.editCvFile?.name || 'Conserver le CV actuel';
    }
    if (type === 'motivation') {
      return this.editMotivationFile?.name || 'Conserver la lettre actuelle';
    }
    return this.editDiplomaFile?.name || 'Conserver le diplôme actuel';
  }

  closeEditModal() {
    this.showEditModal = false;
    this.isSaving = false;
    this.editingApplication = null;
    this.editCvFile = null;
    this.editMotivationFile = null;
    this.editDiplomaFile = null;
    this.editForm = this.createEmptyForm();
  }

  saveApplicationChanges() {
    if (!this.editingApplication?.id) {
      return;
    }

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    const user = this.editForm.user || this.editingApplication.user || (this.userId ? { id: this.userId } : undefined);
    const formData = new FormData();

    formData.append('fullName', this.editForm.fullName || '');
    formData.append('address', this.editForm.address || '');
    formData.append('phoneNumber', this.editForm.phoneNumber || '');
    formData.append('email', this.editForm.email || '');
    formData.append('birthDate', this.editForm.birthDate || '');
    formData.append('birthPlace', this.editForm.birthPlace || '');
    formData.append('nationality', this.editForm.nationality || '');
    formData.append('diploma', this.editForm.diploma || '');
    formData.append('diplomaYear', this.editForm.diplomaYear || '');
    formData.append('institution', this.editForm.institution || '');
    formData.append('experience', this.editForm.experience || '');
    formData.append('technicalSkills', this.editForm.technicalSkills || '');
    formData.append('personalSkills', this.editForm.personalSkills || '');
    formData.append('userId', String(user?.id || this.userId || ''));

    if (this.editCvFile) {
      formData.append('cv', this.editCvFile);
    }
    if (this.editMotivationFile) {
      formData.append('motivationLetter', this.editMotivationFile);
    }
    if (this.editDiplomaFile) {
      formData.append('diplomaFile', this.editDiplomaFile);
    }

    this.isSaving = true;
    this.http.put<ApplicationForm>(`http://localhost:8080/api/v1/applications/${this.editingApplication.id}/with-files`, formData, { headers }).subscribe({
      next: (updated) => {
        this.applications = this.applications.map(app => app.id === updated.id ? { ...app, ...updated } : app);
        this.successMessage = 'Candidature modifiée avec succès.';
        this.errorMessage = '';
        this.closeEditModal();
      },
      error: (err) => {
        console.error('Erreur lors de la modification', err);
        this.errorMessage = 'Erreur lors de la modification de la candidature.';
        this.successMessage = '';
        this.isSaving = false;
      }
    });
  }
}
