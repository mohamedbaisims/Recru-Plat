import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { WelcomeComponent } from './pages/welcome/welcome.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'; // ← AJOUT ReactiveFormsModule
import { RouterModule } from '@angular/router';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { ApplicationFormComponent } from './pages/application-form/application-form.component';
import { ManagerDashboardComponent } from './pages/manager-dashboard/manager-dashboard.component';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { LucideAngularModule } from 'lucide-angular';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { AdminWorkDomainsComponent } from './pages/admin-work-domains/admin-work-domains.component';
import { NgSelectModule } from '@ng-select/ng-select';
import {
  User,
  Mail,
  Phone,
  Globe,
  ArrowRight,
  ArrowLeft,
  Calendar,
  MapPin,
  School,
  FileText
} from 'lucide';
import { CreateManagerComponent } from './pages/create-manager/create-manager.component';
import { ManagerApplicationsComponent } from './pages/manager-applications/manager-applications.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { FilteredCandidatesByDomainComponent } from './pages/filtered-candidates-by-domain/filtered-candidates-by-domain.component';
import { UserCandidaturesComponentComponent } from './pages/user-candidatures-component/user-candidatures-component.component';
import { AdminSettingsComponent } from './pages/admin-settings/admin-settings.component'; //
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';

import { MatSortModule } from '@angular/material/sort';
@NgModule({
  declarations: [

    AppComponent,
    LoginComponent,
    RegisterComponent,
    WelcomeComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    ApplicationFormComponent,
    NavbarComponent,
    SidebarComponent,
    CreateManagerComponent,
    ManagerApplicationsComponent,
    AdminWorkDomainsComponent,
    AdminDashboardComponent,
    FilteredCandidatesByDomainComponent,
    UserCandidaturesComponentComponent,
    AdminSettingsComponent,
    ManagerDashboardComponent
  ],
  imports: [
    CommonModule,
    MatOptionModule,
            MatSelectModule,
            MatTooltipModule,
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule, //
    NgSelectModule,
    // Angular Material Modules
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,

      MatTableModule,


    // Lucide Icons
    LucideAngularModule.pick({
      User,
      Mail,
      Phone,
      Globe,
      ArrowRight,
      ArrowLeft,
      Calendar,
      MapPin,
      School,
      FileText
    })
  ],
  providers: [
    HttpClient,
    provideAnimationsAsync()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
