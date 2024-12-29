import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TokenService } from '../../services/token.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit {
  userEmail: string | null = null;

  constructor(
    private router: Router,
    private tokenService: TokenService
  ) {}

  ngOnInit() {
    this.userEmail = this.tokenService.getEmailFromToken();
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    this.userEmail = null;
    this.router.navigate(['/login']);
  }
}