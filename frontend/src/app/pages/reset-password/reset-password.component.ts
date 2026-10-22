import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from '../../serives/authentication.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
  email: string = '';
  code: string = '';
  newPassword: string = '';
  message: string = '';
  error: string = '';
  success: string = '';
  constructor(
    private authService: AuthenticationService,
    private router: Router
  ) {}


  ngOnInit(): void {
    this.email = localStorage.getItem('reset_email') || '';
  }

  reset() {
    this.authService.resetPassword(this.email, this.code, this.newPassword).subscribe({
      next: () => {
        this.message = 'Mot de passe réinitialisé avec succès.';
        localStorage.removeItem('reset_email');
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
     error: (err) => {
       console.error('Erreur backend complète :', err);
       this.error = err?.error?.message || "Code incorrect ou expiré.";
       this.success = '';
     }
    });
  }
}
