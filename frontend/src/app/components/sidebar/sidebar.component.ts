import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  @Input() isOpen: boolean = true;
userRole: 'ADMIN' | 'USER' | 'MANAGER' | '' = '';  userFullName: string = '';

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken: any = JSON.parse(atob(token.split('.')[1]));
      this.userRole = decodedToken?.role || '';
      this.userFullName = `${decodedToken?.firstname || ''} ${decodedToken?.lastname || ''}`.trim();
    }
  }

  getUserName(): string {
    return this.userFullName || 'Utilisateur';
  }

  getUserRoleDisplay(): string {
    const roleMap: { [key: string]: string } = {
      'USER': 'Candidat',
      'ADMIN': 'Administrateur',
      'MANAGER': 'Manager'
    };
    return roleMap[this.userRole] || this.userRole.toLowerCase();
  }

}
