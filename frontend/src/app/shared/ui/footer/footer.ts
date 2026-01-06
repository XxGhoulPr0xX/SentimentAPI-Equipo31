import { ChangeDetectionStrategy, Component } from '@angular/core';

interface Integrantes {
  fullName: string;
  linkedin: string;
}

@Component({
  selector: 'app-footer',
  imports: [],
  templateUrl: './footer.html',
  styleUrl: './footer.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Footer {
  integrantes: Integrantes[] = [
    {
      fullName: 'Francisco Javier Alvarado Marquez',
      linkedin: '',
    },
    {
      fullName: 'Jose Angel Luna Delgado',
      linkedin: 'https://www.linkedin.com/in/angel-luna468213795',
    },
    {
      fullName: 'Benjamin Gonzalez',
      linkedin: 'https://www.linkedin.com/in/benjamin-gonzalez-93aaa72a1',
    },
    {
      fullName: 'Lourdes Gabriela Sanchez Almaraz',
      linkedin: 'https://www.linkedin.com/in/gabriela-sanchez-dev',
    },
    {
      fullName: 'Gianella Annie Basilio Alvarez',
      linkedin: 'https://www.linkedin.com/in/gianellannie',
    },
    {
      fullName: 'Sergio Valentino De La Cruz Quispe',
      linkedin: '',
    },
    {
      fullName: 'Hugo Ezequiel Sanchez',
      linkedin: 'https://www.linkedin.com/in/hugo-ezequiel-sanchez',
    },
    {
      fullName: 'Augusto Paz',
      linkedin: 'https://github.com/AugustoPaz13',
    },
    {
      fullName: 'Christian Enrique Polo Melendez',
      linkedin: '',
    },
    {
      fullName: 'Diego Quijano Tasayco',
      linkedin: '',
    },
  ];
}
