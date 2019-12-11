import { Controller } from 'stimulus';

import $ from 'jquery';
import echarts from 'echarts';

export default class extends Controller {
  connect() {
    this.startup();
  }

  startup() {
    // 物料来源数量的金额明细，

    $.get('/pages/inbound/matSourceRatio', res => {
      const items = res.items.map(i => ({
        countRatio: i.countRatio.toFixed(2),
        amtRatio: i.amtRatio.toFixed(2)
      }));

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        legend: {
          data: ['1', '2', '3']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value'
        },
        yAxis: {
          type: 'category',
          data: ['料号数量%', '采购金额%']
        },
        series: [
          {
            name: '1',
            type: 'bar',
            stack: '总量',
            label: {
              normal: {
                show: true,
                position: 'insideRight'
              }
            },
            data: [items[0].countRatio, items[0].amtRatio]
          },
          {
            name: '2',
            type: 'bar',
            stack: '总量',
            label: {
              normal: {
                show: true,
                position: 'insideRight'
              }
            },
            data: [items[1].countRatio, items[1].amtRatio]
          },
          {
            name: '3',
            type: 'bar',
            stack: '总量',
            label: {
              normal: {
                show: true,
                position: 'insideRight'
              }
            },
            data: [items[2].countRatio, items[2].amtRatio]
          }
        ]
      };

      const myChart = echarts.init(document.getElementById('matSourceChart'));

      myChart.setOption(option, true);

      myChart.on('click', 'series', params => {
        const url = '/pages/inbound/matMultiSource';
        // window.location.href = url;
        Turbolinks.visit(url);
      });
    });
  }
}
