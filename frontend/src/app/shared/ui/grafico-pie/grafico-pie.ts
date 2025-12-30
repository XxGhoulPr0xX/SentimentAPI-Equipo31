import {
  ChangeDetectionStrategy,
  Component,
  inject,
  Input,
} from '@angular/core';
import { PieChart } from 'echarts/charts';
import { LegendComponent, TooltipComponent } from 'echarts/components';
import * as echarts from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { NgxEchartsDirective, provideEchartsCore } from 'ngx-echarts';

echarts.use([
  PieChart,
  TooltipComponent,
  LegendComponent,
  CanvasRenderer,
  LabelLayout,
]);
import { EChartsCoreOption } from 'echarts/core';
import { LabelLayout } from 'echarts/features';

import { ThemeService } from '../../../core/services/theme-service';

export interface Dato {
  value: number;
  name: string;
}

@Component({
  selector: 'app-grafico-pie',
  imports: [NgxEchartsDirective],
  templateUrl: './grafico-pie.html',
  styleUrl: './grafico-pie.css',
  providers: [provideEchartsCore({ echarts })],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GraficoPie {
  @Input({ required: true }) set datos(datos: Dato[]) {
    this._actualizarGraficoPie(datos);
  }
  private _themeService = inject(ThemeService);
  tema = this._themeService.modoOscuro;
  chartOption: EChartsCoreOption = {};
  temaOscuro = {
    darkMode: true,
    color: ['#35862f', '#d52b33', '#d5aa00'],
    tooltip: {
      backgroundColor: '#1f201d',
      textStyle: {
        color: '#e4e2dd',
      },
    },
  };
  temaClaro = {
    darkMode: false,
    color: ['#34c759', '#ff383c', '#ffcc00'],
    tooltip: {
      backgroundColor: '#e4e2dd',
      textStyle: {
        color: '#1f201d',
      },
    },
  };

  private _actualizarGraficoPie(datos: Dato[]) {
    this.chartOption = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)',
      },
      // legend: {
      //   top: '5%',
      //   left: 'center',
      // },
      series: [
        {
          name: 'Distribución de sentimientos',
          type: 'pie',
          radius: ['40%', '70%'],

          avoidLabelOverlap: false,
          padAngle: 3,
          itemStyle: {
            borderRadius: 4,
          },
          label: {
            show: false,
            position: 'center',
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 20,
              fontWeight: 'bold',
            },
          },
          labelLine: {
            show: false,
          },
          data: datos,
        },
      ],
    };
  }
}
