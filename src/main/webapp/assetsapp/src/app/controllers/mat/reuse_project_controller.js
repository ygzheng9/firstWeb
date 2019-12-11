import { Controller } from 'stimulus';

import axios from 'axios';
import echarts from 'echarts';
import $ from 'jquery';

export default class extends Controller {
  connect() {
    this.startup();
  }

  startup() {
    layui.use(['layer'], () => {
      const { layer } = layui;
      //   tools.echo('called from mat_reuse1');

      const myChart = echarts.init(document.getElementById('reuse1'));
      axios
        .get('/pages/mat/projectMatReuseStats')
        .then(res => res.data)
        .then(res => {
          // console.log(res);
          const items = res.items;

          const levels = items.map(i => i.reuseCount);
          const levelSizeRaw = items.map(i => i.size);
          const levelSize = levelSizeRaw.map(i => {
            let j = i;
            if (i === 1) {
              j = 1.1;
            }
            return Math.log10(j);
          });

          const option = {
            title: {
              top: 'top',
              left: 'center',
              text: '项目-料号复用分析'
            },
            color: ['#3398DB'],
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                // 坐标轴指示器，坐标轴触发有效
                type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
              }
            },
            grid: {
              left: '10%',
              right: '10%',
              bottom: '5%',
              containLabel: true
            },
            xAxis: [
              {
                type: 'category',
                name: '复用次数',
                data: levels,
                axisTick: {
                  alignWithLabel: true
                },
                nameGap: 25,
                nameLocation: 'middle'
              }
            ],
            yAxis: [
              {
                type: 'value',
                name: '料号数量(log10)',
                nameLocation: 'middle',
                nameGap: 25,
                nameRotate: 90
              }
            ],
            series: [
              {
                type: 'bar',
                name: '料号数量',
                barWidth: '60%',
                data: levelSize,
                tooltip: {
                  trigger: 'item',
                  formatter: function(param) {
                    return [
                      '使用次数：' + param.name + '<br/>',
                      '料号数量：' + levelSizeRaw[param.dataIndex]
                    ].join('');
                  }
                }
              }
            ]
          };

          myChart.setOption(option, true);
          myChart.on('click', 'series', params => {
            const matCount = levelSizeRaw[params.dataIndex];
            if (matCount > 300) {
              layer.msg(
                `你不是认真的吧，明细有 ${matCount} 条，真的看得过来么？`,
                {
                  icon: 3,
                  time: 2500,
                  anim: 1
                }
              );
              return;
            }

            const count = params.name;
            const url = '/pages/mat/projectMatByReuseCount?count=' + count;
            xui.setHtml(url, 'matlist');
          });
        });
    });
  }
}
