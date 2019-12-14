import { Controller } from 'stimulus';

import axios from 'axios';
import echarts from 'echarts';

export default class extends Controller {
  connect() {
    this.startup();
  }

  startup() {
    var chart = echarts.init(document.getElementById('mountNode'));

    axios
      .get('/api/wordcloud')
      .then(res => res.data)
      .then(data => {
        const items = data.map(d => {
          return { name: d.name, value: d.count };
        });
        // console.log(items);

        const options = {
          //   backgroundColor: '#fff',
          tooltip: {
            show: true
          },
          series: [
            {
              type: 'wordCloud',
              gridSize: 1,
              sizeRange: [12, 55],
              rotationRange: [-45, 0, 45, 90],
              textStyle: {
                normal: {
                  color: function() {
                    return (
                      'rgb(' +
                      [
                        Math.round(Math.random() * 255),
                        Math.round(Math.random() * 255),
                        Math.round(Math.random() * 255)
                      ].join(',') +
                      ')'
                    );
                  }
                }
              },
              emphasis: {
                shadowBlur: 10,
                shadowColor: '#333'
              },
              left: 'center',
              top: 'center',
              width: '96%',
              height: '100%',
              right: null,
              bottom: null,

              data: items
            }
          ]
        };

        chart.setOption(options, true);
      });
  }
}
