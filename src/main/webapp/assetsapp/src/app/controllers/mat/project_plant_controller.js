import { Controller } from 'stimulus';

import axios from 'axios';
import echarts from 'echarts';

import { compose, map, uniq, find, whereEq } from 'ramda';

import dom from '../zzdom';

export default class extends Controller {
  static targets = ['clientPlantHeat', 'bomList'];

  connect() {
    this.drawChart();
  }

  drawChart() {
    const heatChart = echarts.init(this.clientPlantHeatTarget);

    let plants = [];
    let clients = [];

    axios
      .get('/pages/mat/getClientHeat')
      .then(res => res.data)
      .then(res => {
        const { items } = res;

        plants = compose(
          uniq,
          map(i => i.plant)
        )(items);

        clients = compose(
          uniq,
          map(i => i.client)
        )(items);

        const results = [];
        for (let i = 0; i < plants.length; i++) {
          for (let j = 0; j < clients.length; j++) {
            const v = find(whereEq({ plant: plants[i], client: clients[j] }))(
              items
            );

            let cnt = 0;
            if (v !== undefined) {
              cnt = v.projectCnt;
            }

            const a = [i, j, cnt];
            results.push(a);
          }
        }

        const data = results.map(item => {
          return [item[0], item[1], item[2] || '-'];
        });

        const option = {
          title: {
            top: 'top',
            left: 'center',
            text: '客户-工厂矩阵',
            subtext: '生产布局'
          },
          tooltip: {
            position: 'bottom'
          },
          animation: false,
          grid: {
            left: '10%',
            right: '10%',
            top: '15%',
            bottom: '5%'
          },
          xAxis: {
            type: 'category',
            data: plants,
            splitArea: {
              show: true
            }
          },
          yAxis: {
            type: 'category',
            data: clients,
            splitArea: {
              show: true
            }
          },
          visualMap: {
            min: 0,
            max: 10,
            calculable: true,
            orient: 'horizontal',
            left: 'center',
            bottom: '15%',
            show: false
          },
          series: [
            {
              name: 'projectCnt',
              type: 'heatmap',
              data: data,
              label: {
                normal: {
                  show: true
                }
              },
              itemStyle: {
                emphasis: {
                  shadowBlur: 10,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              },
              tooltip: {
                formatter: function(param) {
                  var data = param.data;
                  return clients[data[1]] + '@' + plants[data[0]];
                }
              }
            }
          ]
        };

        heatChart.setOption(option, true);
        heatChart.on('click', 'series', onClickHeat);
      });

    const targetDom = this.bomListTarget;

    function onClickHeat(params) {
      const data = params.data;
      const plant = plants[data[0]];
      const client = clients[[data[1]]];

      const url = '/pages/mat/bomList?client=' + client + '&plant=' + plant;

      axios
        .get(url)
        .then(res => res.data)
        .then(html => {
          dom.setHtml(html)(targetDom);
        });
    }
  }
}
