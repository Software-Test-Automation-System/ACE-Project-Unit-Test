import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  getDecodedToken() {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
      // JWT tokens are split into three parts by dots
      const payload = token.split('.')[1];
      // Decode the base64 string
      const decodedPayload = atob(payload);
      // Parse the JSON
      return JSON.parse(decodedPayload);
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

  getEmailFromToken(): string | null {
    const decodedToken = this.getDecodedToken();
    return decodedToken ? decodedToken.sub : null;
  }
}