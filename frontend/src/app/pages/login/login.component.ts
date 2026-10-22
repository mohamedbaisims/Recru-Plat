import { Component } from '@angular/core';
import {AuthenticationService} from "../../serives/authentication.service";
import {Router} from "@angular/router";
import {AuthenticationRequest} from "../../models/authentication-request";
import {AuthenticationResponse} from "../../models/authentication-response";
import {VerificationRequest} from "../../models/verification-request";
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  authRequest: AuthenticationRequest={email: '',
                                        password: ''};
  otpCode: any;
  authResponse: AuthenticationResponse={};
  errorMessage: string = '';
  constructor(
    private authService: AuthenticationService,
    private router: Router){
      }
authenticate() {
  this.errorMessage = '';

  this.authService.login(this.authRequest).subscribe({
    next: (response) => {
      this.authResponse = response;

      if (!this.authResponse.mfaEnabled) {
        localStorage.setItem('token', response.accessToken as string);

        // 🔍 Décodage du token pour récupérer le rôle
        const token = response.accessToken as string;
        const payload: any = jwtDecode(token);
        const role = payload?.role;
        if (role === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else if (role === 'MANAGER') {
          this.router.navigate(['/manager-dashboard']);
        } else if (role === 'USER') {
          this.router.navigate(['/application-form']);
        } else {
          console.warn('Rôle inconnu, redirection par défaut');
          this.router.navigate(['/login']);
        }
      }
    },
    error: (err: any) => {
      console.error('Erreur login:', err);
      if (err.status === 404) {
        this.errorMessage = "Adresse email introuvable.";
      } else if (err.status === 401) {
        this.errorMessage = "Mot de passe incorrect.";
      } else {
        this.errorMessage = "Erreur lors de la connexion.";
      }
    }
  });
}

verifyCode() {
  const verifyRequest: VerificationRequest = {
    email: this.authRequest.email,
    code: this.otpCode
  };

  this.authService.verifyCode(verifyRequest).subscribe({
    next: (response) => {
      localStorage.setItem('token', response.accessToken as string);

      // 🔍 Décodage du rôle
      const token = response.accessToken as string;
      const payload: any = jwtDecode(token);
      const role = payload?.role;

      if (role === 'ADMIN') {
        this.router.navigate(['/admin/dashboard']);
      } else if (role === 'USER') {
        this.router.navigate(['/application-form']);
      } else if (role === 'MANAGER') {
        this.router.navigate(['/manager-applications']);
      }
    }
  });
}


navigateToForgotPassword() {
  this.router.navigate(['/forgot-password']);
}

}
