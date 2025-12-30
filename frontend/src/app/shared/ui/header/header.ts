import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
} from '@angular/core';

import { ThemeService } from '../../../core/services/theme-service';
import { CampoSeleccion, Valores } from '../campo-seleccion/campo-seleccion';

@Component({
  selector: 'app-header',
  imports: [CampoSeleccion],
  templateUrl: './header.html',
  styleUrl: './header.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Header implements OnInit {
  private _themeService = inject(ThemeService);

  idiomas: Valores[] = [
    { value: 'es', viewValue: 'Español' },
    { value: 'en', viewValue: 'English' },
    { value: 'fr', viewValue: 'Français' },
  ];
  modos: Valores[] = [
    { value: 'claro', viewValue: 'Modo Claro' },
    { value: 'oscuro', viewValue: 'Modo Oscuro' },
  ];
  modo = 'claro';
  idioma = 'es';

  ngOnInit() {
    this.modo = this._themeService.modoOscuro() ? 'oscuro' : 'claro';
    this.cambiarModo(this.modo);
  }
  cambiarModo(nuevoValor: string) {
    this.modo = nuevoValor;
    if (this.modo === 'oscuro') {
      this._themeService.setModoOscuro(true);
    } else {
      this._themeService.setModoOscuro(false);
    }
  }
  cambiarIdioma(nuevoValor: string) {
    this.idioma = nuevoValor;
  }
}
