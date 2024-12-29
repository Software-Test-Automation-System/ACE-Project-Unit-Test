import { Component } from '@angular/core';
import { NavbarComponent } from "../navbar/navbar.component";
import { FooterComponent } from "../footer/footer.component";
import { RouterOutlet } from '@angular/router';
import { TestPageComponent } from '../../component/test-page/test-page.component';

@Component({
  selector: 'app-page-content',
  standalone: true,
  imports: [TestPageComponent , RouterOutlet, NavbarComponent, FooterComponent],
  templateUrl: './page-content.component.html',
  styleUrl: './page-content.component.scss'
})
export class PageContentComponent {

}
