import { Component } from '@angular/core';
import {RegisterRequest} from "../../models/register-request";
import {AuthenticationResponse} from "../../models/authentication-response";
import {AuthenticationService} from "../../serives/authentication.service";
import {Router} from "@angular/router";
import {VerificationRequest} from "../../models/verification-request";
@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerRequest: RegisterRequest = {
    firstname: '',
    lastname: '',
    email: '',
    password: '',
    role: 'USER'
  };

  confirmPassword: string = '';
  message: string = '';
  authResponse: AuthenticationResponse = {};
  passwordMismatch: boolean = false;

  constructor(private authService: AuthenticationService, private router: Router) {}

  registerUser() {
    this.message = '';
    this.passwordMismatch = this.registerRequest.password !== this.confirmPassword;

    if (this.passwordMismatch) return;

    this.authService.register(this.registerRequest).subscribe({
      next: (response) => {
        this.authResponse = response;
        this.message = 'Compte créé avec succès !';
        // ✅ Rediriger après quelques secondes (facultatif : mettre un délai)
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.message = 'Erreur lors de la création du compte.';
        console.error("Erreur backend :", err);
      }
    });
  }
}
