import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-manager',
  templateUrl: './create-manager.component.html',
  styleUrls: ['./create-manager.component.scss']
})
export class CreateManagerComponent {
  form = {
    firstname: '',
    lastname: '',
    email: '',
    password: ''
  };

  message = '';
  error = '';

  constructor(private http: HttpClient, private router: Router) {}

 createManager() {
   this.http.post('http://localhost:8080/api/v1/manager/create', this.form, {
     headers: {
       Authorization: `Bearer ${localStorage.getItem('token')}`
     },
     responseType: 'text' // 👈 Important pour recevoir une chaîne
   }).subscribe({
     next: (res: any) => {
       this.message = res; // 'Manager créé avec succès !'
       this.error = '';
       this.form = { firstname: '', lastname: '', email: '', password: '' };
     },
     error: (err) => {
       this.error = err.error || 'Erreur lors de la création';
       this.message = '';
     }
   });
 }
}
