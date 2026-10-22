import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  isSidebarOpen = true;

  constructor(public router: Router) {}

  toggleSidebar = () => {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  shouldShowLayout(): boolean {
    const hiddenRoutes = ['/login', '/register', '/forgot-password'];
    return !hiddenRoutes.includes(this.router.url);
  }
}
