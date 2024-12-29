import { Routes } from '@angular/router';
import { TestPageComponent } from './component/test-page/test-page.component';
import { LoginComponent } from './component/login/login.component';
import { RegisterComponent } from './component/register/register.component';
import { AuthGuard } from './auth.guard';
import { MainLayoutComponent } from './component/main-layout/main-layout.component';

export const routes: Routes = [
    {
        path: '', 
        component: MainLayoutComponent,
        canActivate: [AuthGuard]  
      },    {path: 'register',component:RegisterComponent},
    {path:'login',component:LoginComponent},
];
