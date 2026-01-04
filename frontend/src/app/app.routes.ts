import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'inicio',
    loadComponent: () => import('./routes/inicio/inicio').then((m) => m.Inicio),
  },
  {
    path: '**',
    redirectTo: 'inicio',
    pathMatch: 'prefix',
  },
];
