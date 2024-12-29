import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoginServiceService {

  private apiUrl = 'http://localhost:8085/auth/login';
  constructor(private http: HttpClient) { }

  authenticationService(queryParams: string):Observable<any>{
    const url = `${this.apiUrl}${queryParams}`;
    return this.http.post(url ,null);
  }
}
