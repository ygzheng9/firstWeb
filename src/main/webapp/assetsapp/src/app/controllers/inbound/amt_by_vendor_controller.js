import { Controller } from 'stimulus';

import $ from 'jquery';
import echarts from 'echarts';
import _ from 'lodash';
import numeral from 'numeral';

export default class extends Controller {
  connect() {
    this.startup();
  }

  startup() {
    layui.use(['layer'], () => {
      const { layer } = layui;

      // 供应商列表
      const drillByGradeDom = $('#drillByGrade');
      $('#for-drillByGrade').on('click', 'a[data-remote]', function() {
        const self = $(this);
        const url = self.attr('data-href');

        const count = self.attr('data-params');
        if (count > 300) {
          layer.msg(`你不是认真的吧，明细有 ${count} 条，真的看得过来么？`, {
            icon: 3,
            time: 2500,
            anim: 1
          });
          return;
        }

        axios
          .get(url)
          .then(res => res.data)
          .then(res => {
            drillByGradeDom.html(res);
          });

        return false;
      });

      // 工厂列表
      const plantListDom = $('#plantList');
      drillByGradeDom.on('click', 'a[data-remote]', function() {
        const self = $(this);
        const url = self.attr('data-href');

        axios
          .get(url)
          .then(res => res.data)
          .then(res => {
            plantListDom.html(res);

            layer.open({
              type: 1,
              title: '工厂明细',
              area: ['800px', '450px'],
              content: res
            });
          });

        return false;
      });

      function drawChart() {
        // 入库 和 bom 料号 匹配的数量，金额
        const domid = $('#amtChartData');
        const raw = domid.attr('data-params');
        const dataRaw = JSON.parse(raw);

        // 计算总金额，占比
        const totalAmt = _.sum(dataRaw.map(i => i.totalAmt));
        const data = dataRaw.map(i => ({
          ...i,
          pect: (100 * i.cumAmt) / totalAmt
        }));

        const option = {
          title: {
            text: '供应商采购分析',
            top: 'top',
            left: 'center'
          },
          dataset: {
            dimensions: [
              'vendorCode',
              'vendorName',
              'totalAmt',
              'cumAmt',
              'pect'
            ],
            source: data
          },
          tooltip: {
            // trigger: 'item'
            trigger: 'axis',
            formatter: function(param) {
              //   console.log(param);

              const entry = param[0].data;
              return [
                '供应商：' + entry.vendorName + '<br/>',
                '金额: ' + numeral(entry.totalAmt).format('0,0') + '<br/>',
                '累计占比%：' + numeral(entry.pect).format('0,0.00')
              ].join('');
            }
          },
          grid: {
            bottom: '10%'
          },
          xAxis: {
            type: 'category',
            name: '供应商',
            nameLocation: 'middle',
            axisLabel: {
              show: false
            }
          },
          yAxis: [
            {
              type: 'value',
              name: '金额',
              position: 'left',
              min: -10000
            },
            {
              type: 'value',
              name: '占比',
              position: 'right',
              min: 0,
              max: 110,
              offset: 0
            }
          ],
          series: [
            {
              name: '金额',
              type: 'bar',
              yAxisIndex: 0,
              encode: {
                // Map the "amount" column to X axis.
                x: 'vendorCode',
                // Map the "product" column to Y axis
                y: 'totalAmt'
              },
              itemStyle: {
                // 柱状图颜色
                color: '#4ad2ff'
              }
            },
            {
              name: '占比',
              type: 'line',
              yAxisIndex: 1,
              encode: {
                // Map the "amount" column to X axis.
                x: 'vendorCode',
                // Map the "product" column to Y axis
                y: 'pect'
              },
              itemStyle: {
                // 柱状图颜色
                color: '#E15457'
              }
            }
          ]
        };

        const amtChart = echarts.init(document.getElementById('amtChart'));

        amtChart.setOption(option, true);
      }

      drawChart();
    });
  }
}
