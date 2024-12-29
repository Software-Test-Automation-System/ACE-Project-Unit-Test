import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PageContentComponent } from './shared/page-content/page-content.component';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { RouterOutlet } from '@angular/router';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'ace-front';

 
}
