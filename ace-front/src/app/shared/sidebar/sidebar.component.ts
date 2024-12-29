import { Component} from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TokenService } from '../../services/token.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink,RouterLinkActive,CommonModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  
  history: any[] = []; // Array to store the API response

  constructor(private http: HttpClient,    private tokenService: TokenService
  ) {}

  truncateInput(input: string): string {
    return input.length > 15 ? input.slice(0, 15) + '...' : input;
  }

  ngOnInit(): void {
    this.fetchHistory();
  }

  fetchHistory(): void {
    const email = this.tokenService.getEmailFromToken();
    
    if (!email) {
      console.error('No email found in token');
      return;
    }

    const apiUrl = `http://localhost:8083/unitTest/history?email=${encodeURIComponent(email)}`;
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    this.http.get<any[]>(apiUrl, { headers }).subscribe({
        next: (data) => {
            this.history = data.map(item => ({
                ...item,
                input: this.truncateInput(item.input)
            }));
        },
        error: (err) => {
            console.error('Error fetching history:', err);
        }
    });
  }


}
