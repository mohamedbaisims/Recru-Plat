import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { WorkDomain } from '../../models/work-domain.model';

@Component({
  selector: 'app-admin-work-domains',
  templateUrl: './admin-work-domains.component.html',
  styleUrls: ['./admin-work-domains.component.scss']
})
export class AdminWorkDomainsComponent implements OnInit {

  domains: WorkDomain[] = [];
  newDomain: string = '';
  newKeywords: string = '';
  errorMessage: string = '';
  editingDomainId: number | null = null;
  editingKeywords: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadDomains();
  }

  loadDomains(): void {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<WorkDomain[]>('http://localhost:8080/api/v1/work-domains', { headers })
      .subscribe({
        next: (data) => {
          this.domains = data;
        },
        error: (err) => {
          console.error("Erreur lors du chargement des domaines", err);
        }
      });
  }

  addDomain(): void {
    if (!this.newDomain.trim()) {
      this.errorMessage = 'Le nom du domaine est obligatoire.';
      return;
    }

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    const domain: WorkDomain = {
      name: this.newDomain.trim(),
      keywords: this.newKeywords.trim() || undefined
    };

    this.http.post('http://localhost:8080/api/v1/work-domains', domain, { headers })
      .subscribe({
        next: () => {
          console.log("Domaine ajouté");
          this.newDomain = '';
          this.newKeywords = '';
          this.errorMessage = '';
          this.loadDomains();
        },
        error: (err) => {
          console.error("Erreur lors de l'ajout du domaine", err);
          this.errorMessage = "Impossible d'ajouter le domaine pour le moment.";
        }
      });
  }

  deleteDomain(id: number): void {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.delete(`http://localhost:8080/api/v1/work-domains/${id}`, { headers })
      .subscribe({
        next: () => {
          console.log("Domaine supprimé");
          this.loadDomains();
        },
        error: (err) => {
          console.error("Erreur lors de la suppression du domaine", err);
        }
      });
  }

  startEditing(domain: WorkDomain): void {
    this.editingDomainId = domain.id ?? null;
    this.editingKeywords = domain.keywords ?? '';
    this.errorMessage = '';
  }

  cancelEditing(): void {
    this.editingDomainId = null;
    this.editingKeywords = '';
  }

  saveKeywords(domain: WorkDomain): void {
    if (!domain.id) {
      return;
    }

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    const updatedDomain: WorkDomain = {
      ...domain,
      keywords: this.editingKeywords.trim() || ''
    };

    this.http.put<WorkDomain>(`http://localhost:8080/api/v1/work-domains/${domain.id}`, updatedDomain, { headers })
      .subscribe({
        next: (savedDomain) => {
          this.domains = this.domains.map(item => item.id === savedDomain.id ? savedDomain : item);
          this.cancelEditing();
        },
        error: (err) => {
          console.error("Erreur lors de la mise à jour des mots-clés", err);
          this.errorMessage = "Impossible de mettre à jour les mots-clés pour le moment.";
        }
      });
  }
}
