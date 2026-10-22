import { Component, OnInit } from '@angular/core';
import { ApplicationFormService } from '../../serives/application-form.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-manager-dashboard',
  templateUrl: './manager-dashboard.component.html',
  styleUrls: ['./manager-dashboard.component.scss']
})
export class ManagerDashboardComponent implements OnInit {

  stats: any={};
loading: boolean = true;

  constructor(private appService: ApplicationFormService,private router: Router) {} // ✅ injection

  ngOnInit(): void {
    this.appService.getDashboardStats().subscribe({
      next: (data: any) => { // ✅ typer data
          const totalFromDomains = data.byDomain
            .reduce((sum: number, d: any) => sum + d.count, 0);

          this.stats = {
            ...data,
            total: totalFromDomains + data.unassigned
          };
        this.loading=false;
      },
      error: (err:any) => {
        console.error(err);
        this.loading=false;
      }
    });
  }
 goToCandidatesByDomain() {
    this.router.navigate(['/manager-applications']);
  }
}
