import { Component, OnInit } from '@angular/core';
import { DashboardService, DashboardStats } from '../../serives/dashboard.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-dashboard',
  standalone: false,
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  stats: DashboardStats | null = null;
  isLoading: boolean = true;
  errorMessage: string = '';
  domainChartData: { name: string; count: number }[] = [];

  constructor(private dashboardService: DashboardService) { }

  ngOnInit(): void {
    this.loadStatistics();
  }

  loadStatistics(): void {
    this.isLoading = true;
    this.dashboardService.getStatistics().subscribe({
      next: (data) => {
        this.stats = data;
        this.isLoading = false;
        // Préparer les données pour le graphique des domaines
        this.prepareDomainChartData();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des statistiques:', error);
        this.errorMessage = 'Erreur lors du chargement des statistiques. Veuillez recharger la page.';
        this.isLoading = false;
      }
    });
  }

  prepareDomainChartData(): void {
    if (this.stats && this.stats.candidaturesByDomain) {
      this.domainChartData = Object.entries(this.stats.candidaturesByDomain).map(([name, count]) => ({
        name,
        count
      }));
    }
  }

  // Méthode pour obtenir la couleur basée sur l'index
  getChartColor(index: number): string {
    const colors = [
      '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF',
      '#FF9F40', '#FF6384', '#C9CBCF', '#4BC0C0', '#FF6384'
    ];
    return colors[index % colors.length];
  }

  // Obtenir la largeur de la barre (en pourcentage du total max)
  getBarWidth(count: number): number {
    const maxCount = Math.max(...this.domainChartData.map(d => d.count), 1);
    return (count / maxCount) * 100;
  }
}
