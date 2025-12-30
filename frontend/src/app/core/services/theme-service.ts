import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  modoOscuro = signal<boolean>(false);

  constructor() {
    const temaGuardado = localStorage.getItem('tema');
    if (temaGuardado === 'oscuro') {
      this.modoOscuro.set(true);
    } else if (temaGuardado === 'claro') {
      this.modoOscuro.set(false);
    }
  }

  setModoOscuro(isModoOscuro: boolean) {
    this.modoOscuro.set(isModoOscuro);
    if (isModoOscuro) {
      document.body.classList.add('tema-oscuro');
      localStorage.setItem('tema', 'oscuro');
    } else {
      document.body.classList.remove('tema-oscuro');
      localStorage.setItem('tema', 'claro');
    }
  }
}
