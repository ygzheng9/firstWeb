import { Controller } from 'stimulus';

import $ from 'jquery';
import echarts from 'echarts';

import numeral from 'numeral';

export default class extends Controller {
  connect() {
    this.startup();
  }

  startup() {
    const domid = $('#byVendors');
    const raw = domid.attr('data-params');
    const items = JSON.parse(raw);

    const amts = items.map(i => ({
      name: i.vendorName,
      value: i.totalAmt
    }));

    const vols = items.map(i => ({
      name: i.vendorName,
      value: i.totalQty
    }));

    const option = {
      tooltip: {
        trigger: 'item',
        formatter: function(params) {
          const { seriesName, data, percent } = params;
          return [
            seriesName + '<br/>',
            data.name + '<br/>',
            numeral(data.value).format('0,0.00') + '<br/>',
            numeral(percent).format('0.00') + '%'
          ].join('');
        }
      },
      series: [
        {
          name: '金额占比',
          type: 'pie',
          selectedMode: 'single',
          radius: [0, '30%'],

          label: {
            normal: {
              position: 'inner'
            }
          },
          labelLine: {
            normal: {
              show: false
            }
          },
          data: amts
        },
        {
          name: '采购量占比',
          type: 'pie',
          radius: ['40%', '55%'],
          label: {
            normal: {
              formatter: '{a|{a}}{abg|}\n{hr|}\n  {b|{b}：}{c}  {per|{d}%}  ',
              backgroundColor: '#eee',
              borderColor: '#aaa',
              borderWidth: 1,
              borderRadius: 4,
              rich: {
                a: {
                  color: '#999',
                  lineHeight: 22,
                  align: 'center'
                },
                hr: {
                  borderColor: '#aaa',
                  width: '100%',
                  borderWidth: 0.5,
                  height: 0
                },
                b: {
                  fontSize: 16,
                  lineHeight: 33
                },
                per: {
                  color: '#eee',
                  backgroundColor: '#334455',
                  padding: [2, 4],
                  borderRadius: 2
                }
              }
            }
          },
          data: vols
        }
      ]
    };

    const myChart = echarts.init(document.getElementById('byVendorsChart'));

    myChart.setOption(option, true);
  }
}
