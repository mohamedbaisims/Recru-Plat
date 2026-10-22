import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { ApplicationFormComponent } from './pages/application-form/application-form.component';
import { LoginComponent } from './pages/login/login.component';

import { RegisterComponent } from './pages/register/register.component';
import { WelcomeComponent } from './pages/welcome/welcome.component';
import { CreateManagerComponent } from './pages/create-manager/create-manager.component';
import { ManagerApplicationsComponent } from './pages/manager-applications/manager-applications.component';
import { AdminWorkDomainsComponent } from './pages/admin-work-domains/admin-work-domains.component';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard.component';
import { authGuard } from "./serives/auth/auth.guard";
import { FilteredCandidatesByDomainComponent } from './pages/filtered-candidates-by-domain/filtered-candidates-by-domain.component';
import { UserCandidaturesComponentComponent } from './pages/user-candidatures-component/user-candidatures-component.component';
import { AdminSettingsComponent } from './pages/admin-settings/admin-settings.component';
import{ManagerDashboardComponent} from './pages/manager-dashboard/manager-dashboard.component';
const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'welcome', component: WelcomeComponent, canActivate: [authGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'application-form', component: ApplicationFormComponent, canActivate: [authGuard] },
  {
    path: 'admin/dashboard',
    component: AdminDashboardComponent,
    canActivate: [authGuard],
    data: { role: 'ADMIN' }
  },
  {
    path: 'create-manager',
    component: CreateManagerComponent,
    canActivate: [authGuard],
    data: { role: 'ADMIN' }
  },
  {
    path: 'manager-applications',
    component: ManagerApplicationsComponent,
    canActivate: [authGuard],
    data: { role: 'MANAGER' }
  },
{
    path: 'manager-dashboard',
    component: ManagerDashboardComponent,
    canActivate: [authGuard],
    data: { role: 'MANAGER' }
  },

  {
    path: 'my-candidatures',
    component: UserCandidaturesComponentComponent,
    canActivate: [authGuard]
  },
  {
    path: 'admin/work-domains',
    component: AdminWorkDomainsComponent,
    canActivate: [authGuard],
    data: { role: 'ADMIN' }
  },
  {
    path: 'manager/candidates-by-domain',
    component: FilteredCandidatesByDomainComponent,
    canActivate: [authGuard],
    data: { role: 'MANAGER' }
  },
  {
    path: 'admin/settings',
    component: AdminSettingsComponent,
    canActivate: [authGuard],
    data: { role: 'ADMIN' }
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
