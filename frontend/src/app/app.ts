import { registerLocaleData } from '@angular/common';
import localeEsPe from '@angular/common/locales/es-PE';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

registerLocaleData(localeEsPe, 'es-PE');

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
