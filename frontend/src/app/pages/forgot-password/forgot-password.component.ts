import { Component } from '@angular/core';
import { AuthenticationService } from '../../serives/authentication.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent {
  email: string = '';
  message: string = '';
  error: string = '';

  constructor(
    private authService: AuthenticationService,
    private router: Router
  ) {}

 onSubmit() {
   this.authService.forgotPassword(this.email).subscribe({
     next: () => {
       this.error = '';
       localStorage.setItem('reset_email', this.email);
       this.router.navigate(['/reset-password']);
     },
     error: (err) => {
       this.error = "Erreur d'envoi du code";
       console.error("Erreur backend :", err);
     }
   });
 }



}
