import { Component, OnInit } from '@angular/core';
import { ApplicationFormDTO } from '../../models/application-form-dto';
import { ApplicationFormService } from '../../serives/application-form.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { WorkDomain } from '../../models/work-domain.model';

interface CvAnalysisResult {
  domaine: string;
  domaineSuggere: string | null;
  domaineId: number | null;
  score: number;
  matchedKeywords: string[];
  confidence: string;
}

@Component({
  selector: 'app-manager-applications',
  templateUrl: './manager-applications.component.html',
  styleUrls: ['./manager-applications.component.scss'] // <- corrigé ici
})
export class ManagerApplicationsComponent implements OnInit {

  applications: ApplicationFormDTO[] = []; // <- type corrigé
  errorMessage: string = ''; // <- ajouté pour gérer les erreurs
aiResults: { [key: number]: CvAnalysisResult } = {};
 displayedColumns: string[] = [
   'fullName',
   'address',
   'diploma',
   'experience',
   'technicalSkills',
   'personalSkills',
   'documents',
   'aiResult', // 🔥 nouvelle colonne
   'domainSelect',
   'actions'
 ];
selectedDomains: { [key: number]: number } = {}; // key = app.id, value = domainId

  constructor(private appService: ApplicationFormService,private http: HttpClient) {}
workDomains: WorkDomain[] = [];

  ngOnInit(): void {
    this.appService.getUnassignedApplications().subscribe({
      next: (data) => this.applications = data,
      error: () => this.errorMessage = "Erreur lors du chargement des candidatures."
    });


    this.http.get<WorkDomain[]>('http://localhost:8080/api/v1/work-domains', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    }).subscribe({
      next: (data) => this.workDomains = data,
      error: (err) => console.error("Erreur lors du chargement des domaines", err)
    });
  }


  assignDomain(applicationId: number): void {
    const domainId = this.selectedDomains[applicationId];
    if (!domainId) return;

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.put(
      `http://localhost:8080/api/v1/applications/${applicationId}/assign-domain/${domainId}`,
      {},
      { headers }
    ).subscribe({
      next: () => {
        console.log("✅ Domaine assigné !");
         this.applications = this.applications.filter(app => app.id !== applicationId);
      },
      error: (err) => {
        console.error("❌ Erreur lors de l’assignation du domaine :", err);
      }
    });
  }

  getPdfUrl(file: number[] | Uint8Array): string {
    const byteArray = new Uint8Array(file);
    const blob = new Blob([byteArray], { type: 'application/pdf' });
    return URL.createObjectURL(blob);
  }
getSkillsArray(skills: string): string[] {
  if (!skills) return [];
  return skills.split(',').map(skill => skill.trim()).filter(skill => skill.length > 0);
}
openFile(applicationId: number, type: string): void {
  this.appService.getFile(applicationId, type).subscribe({
    next: (fileBlob) => {
      const url = URL.createObjectURL(fileBlob);
      window.open(url, '_blank');
    },
    error: (err) => {
      console.error('Erreur lors du chargement du fichier :', err);
      alert("Fichier introuvable ou corrompu.");
    }
  });
}
analyserCV(app: ApplicationFormDTO): void {

  this.appService.getFile(app.id, 'cv').subscribe({
    next: (fileBlob) => {

      const formData = new FormData();
      formData.append('file', fileBlob, 'cv.pdf');

      this.http.post<CvAnalysisResult>('http://localhost:8080/api/ai/analyser', formData)
        .subscribe({
          next: (res) => {
            console.log("✅ Résultat IA :", res);

            this.aiResults[app.id] = res;

            const matchedDomain = this.workDomains.find(d => d.id === res.domaineId)
              ?? this.workDomains.find(d => d.name === res.domaineSuggere);

            if (matchedDomain) {
              this.selectedDomains[app.id] = matchedDomain.id!;
            }

          },
          error: (err) => {
            console.error("❌ Erreur IA :", err);
          }
        });

    },
    error: (err) => {
      console.error("❌ Erreur récupération CV :", err);
    }
  });
}
}
