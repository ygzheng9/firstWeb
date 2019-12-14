import { Controller } from 'stimulus';

import axios from 'axios';
import echarts from 'echarts';

import { pluck, map } from 'ramda';

import zzdom from '../zzdom';

export default class extends Controller {
  connect() {
    this.startup();
  }

  startup() {
    layui.use(['layer'], () => {
      const { layer } = layui;
      const myChart = echarts.init(zzdom.byId('bomreuse'));

      axios
        .get('/pages/mat/reuseByBomData')
        .then(res => res.data)
        .then(res => {
          // console.log(res);
          const items = res.items;

          const levels = pluck('repeatedCnt')(items);
          const levelSizeRaw = pluck('size')(items);
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
              text: '料号复用分析',
              subtext: 'By BOM'
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
            const url = '/pages/mat/bomMatByReuse?count=' + count;

            zzdom.setHtmlUrl(url)(zzdom.byId('matlist'));
          });
        });
    });
  }
}
