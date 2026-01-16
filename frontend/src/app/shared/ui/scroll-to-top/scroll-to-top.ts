import {
  ChangeDetectionStrategy,
  Component,
  HostListener,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-scroll-to-top',
  imports: [MatButtonModule, MatIcon],
  templateUrl: './scroll-to-top.html',
  styleUrl: './scroll-to-top.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ScrollToTop {
  showScrollButton = false;

  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.showScrollButton = window.pageYOffset > 100;
  }

  scrollToTop() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
