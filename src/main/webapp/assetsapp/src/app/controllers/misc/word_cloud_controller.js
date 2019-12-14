import { Controller } from 'stimulus';

import $ from 'jquery';
import G2 from '@antv/g2';
import _ from 'lodash';
import axios from 'axios';

import { View } from '@antv/data-set';

export default class extends Controller {
  connect() {
    this.startup();
  }

  startup() {
    function getTextAttrs(cfg) {
      return _.assign({}, cfg.style, {
        fillOpacity: cfg.opacity,
        fontSize: cfg.origin._origin.size,
        rotate: cfg.origin._origin.rotate,
        text: cfg.origin._origin.text,
        textAlign: 'center',
        fontFamily: cfg.origin._origin.font,
        fill: cfg.color,
        textBaseline: 'Alphabetic'
      });
    }

    // 给point注册一个词云的shape
    G2.Shape.registerShape('point', 'cloud', {
      drawShape: function drawShape(cfg, container) {
        var attrs = getTextAttrs(cfg);
        return container.addShape('text', {
          attrs: _.assign(attrs, {
            x: cfg.x,
            y: cfg.y
          })
        });
      }
    });

    axios
      .get('/api/wordcloud')
      .then(res => res.data)
      .then(data => {
        $('#mountNode').text('');

        var dv = new View().source(data);
        var range = dv.range('count');
        var min = range[0];
        var max = range[1];
        dv.transform({
          type: 'tag-cloud',
          fields: ['name', 'count'],
          size: [window.innerWidth, window.innerHeight],
          font: 'Verdana',
          padding: 0,
          timeInterval: 5000, // max execute time
          rotate: function rotate() {
            var random = ~~(Math.random() * 4) % 4;
            if (random == 2) {
              random = 0;
            }
            return random * 90; // 0, 90, 270
          },
          fontSize: function fontSize(d) {
            if (d.value) {
              return ((d.value - min) / (max - min)) * (80 - 24) + 24;
            }
            return 0;
          }
        });
        var chart = new G2.Chart({
          container: 'mountNode',
          width: window.innerWidth,
          height: window.innerHeight,
          padding: 0
        });
        chart.source(dv, {
          x: {
            nice: false
          },
          y: {
            nice: false
          }
        });
        chart.legend(false);
        chart.axis(false);
        chart.tooltip({
          showTitle: false
        });
        chart.coord().reflect();
        // chart.point().position('x*y').color('category').shape('cloud').tooltip('value*category');
        chart
          .point()
          .position('x*y')
          .color('x')
          .shape('cloud')
          .tooltip('count');
        chart.render();
      });
  }
}
