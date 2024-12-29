import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginServiceService } from '../../services/login-service.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule,FormsModule,CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  loginForm: FormGroup;
  constructor(    private fb: FormBuilder, private loginService: LoginServiceService, private router: Router
  ){
    this.loginForm = this.fb.group({
      email: ['',Validators.required],
      password: ['', Validators.required]
    });
  }
  onSubmit():void{
    if(this.loginForm.valid){
      const loginData = this.loginForm.value;
      console.log('Login :', loginData);
      this.router.navigate(['/']);

    }else{
      console.log('Form is invalid');
    }
  const formValues = this.loginForm.value;
    const email = formValues.email;
    const password = formValues.password;
    
    const queryParams = `?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`;

   this.loginService.authenticationService(queryParams).subscribe({
      next: (response) => {
        console.log('authentication response',response);
    localStorage.setItem("token", response.accessToken);
    localStorage.setItem("refreshToken", response.refreshToken);
    this.router.navigate(['/']);

    console.log('User ID, Access Token, and Refresh Token saved to localStorage.');
      }
    }); 

  }
  
}
