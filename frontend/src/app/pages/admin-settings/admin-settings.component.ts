import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

interface Manager {
  id: number;
  firstname: string;
  lastname: string;
  email: string;
}

@Component({
  selector: 'app-admin-settings',
  templateUrl: './admin-settings.component.html',
  styleUrls: ['./admin-settings.component.scss']
})
export class AdminSettingsComponent implements OnInit {
  managers: Manager[] = [];
  filteredManagers: Manager[] = [];
  selectedManager: Manager | null = null;
  showManagersDropdown = false;
  managerSearch = '';

  passwordForm: FormGroup;
  showNewPassword = false;
  showConfirmPassword = false;
  isLoading = false;
  statusMessage = '';
  isSuccess = false;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient
  ) {
    this.passwordForm = this.createPasswordForm();
  }

  ngOnInit(): void {
    this.loadManagers();
  }

  createPasswordForm(): FormGroup {
    return this.fb.group({
      newPassword: ['', [
        Validators.required,
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/)
      ]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(control: AbstractControl) {
    const password = control.get('newPassword');
    const confirmPassword = control.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { mismatch: true };
    }
    return null;
  }

  loadManagers(): void {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.get<Manager[]>('http://localhost:8080/api/v1/manager', { headers }).subscribe({
      next: (data) => {
        this.managers = data;
        this.filteredManagers = data;
      },
      error: (err) => {
        console.error('Erreur chargement managers', err);
        this.showStatus('Erreur lors du chargement des managers', false);
      }
    });
  }

  toggleManagersDropdown(): void {
    this.showManagersDropdown = !this.showManagersDropdown;
    if (this.showManagersDropdown) {
      this.managerSearch = '';
      this.filteredManagers = this.managers;
    }
  }

  filterManagers(): void {
    if (!this.managerSearch.trim()) {
      this.filteredManagers = this.managers;
    } else {
      const term = this.managerSearch.toLowerCase();
      this.filteredManagers = this.managers.filter(manager =>
        manager.email.toLowerCase().includes(term) ||
        manager.firstname.toLowerCase().includes(term) ||
        manager.lastname.toLowerCase().includes(term)
      );
    }
  }

  selectManager(manager: Manager): void {
    this.selectedManager = manager;
    this.showManagersDropdown = false;
    this.managerSearch = '';
    this.passwordForm.reset();
  }

  selectManagerForEdit(manager: Manager): void {
    this.selectedManager = manager;
    this.passwordForm.reset();
    // Scroll vers le formulaire
    setTimeout(() => {
      const formElement = document.querySelector('.password-form');
      if (formElement) {
        formElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    }, 100);
  }

  updateManagerPassword(): void {
    if (this.passwordForm.invalid || !this.selectedManager) {
      return;
    }

    this.isLoading = true;
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const payload = {
      newPassword: this.passwordForm.get('newPassword')?.value
    };

    this.http.put(
      `http://localhost:8080/api/v1/managers/${this.selectedManager.id}/password`,
      payload,
      { headers }
    ).subscribe({
      next: () => {
        this.isLoading = false;
        this.showStatus('Mot de passe mis à jour avec succès', true);
        this.passwordForm.reset();
        this.selectedManager = null;
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Erreur mise à jour mot de passe', err);
        this.showStatus('Erreur lors de la mise à jour du mot de passe', false);
      }
    });
  }

  cancelUpdate(): void {
    this.selectedManager = null;
    this.passwordForm.reset();
    this.statusMessage = '';
  }

  showStatus(message: string, success: boolean): void {
    this.statusMessage = message;
    this.isSuccess = success;

    setTimeout(() => {
      this.statusMessage = '';
    }, 5000);
  }
}
