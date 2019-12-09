(() => {
  function drawChart() {
    let myChart = echarts.init(document.getElementById('vendorPlantDiv'));

    const url = '/pages/relation/vendorPlantData';
    $.get(url, res => {
      const { nodes, links } = res.data;

      const categories = [{ name: 'vendor' }, { name: 'plant' }];

      nodes.forEach(node => {
        node.value = node.amt;

        if (node.category === 'vendor') {
          node.symbolSize = Math.abs(node.amt / 1000.0);
        } else if (node.category === 'plant') {
          node.symbolSize = Math.abs(node.amt / 1000.0);
        }

        if (node.symbolSize < 3) {
          node.symbolSize = 3;
        }

        node.draggable = true;

        node.label = {
          normal: {
            show: node.symbolSize > 15
          }
        };
      });

      let option = {
        //   backgroundColor: '#FFFFFF',
        animationDuration: 0,
        animationEasingUpdate: 'quinticInOut',
        tooltip: {
          formatter: '{b}'
        },
        series: [
          {
            type: 'graph',

            categories: categories,

            layout: 'force',
            force: {
              repulsion: 150,
              gravity: 0.5,
              edgeLength: 10,
              layoutAnimation: true
            },
            symbolSize: 10,
            roam: true,
            focusNodeAdjacency: true,
            emphasis: {
              lineStyle: {
                width: 5
              }
            },
            label: {
              normal: {
                show: true,
                position: 'bottom',
                formatter: '{b}',
                fontSize: 10,
                fontStyle: '300'
              }
            },
            lineStyle: {
              normal: {
                opacity: 0.9,
                width: 0.5,
                curveness: 0
              }
            },

            data: nodes,
            links: links
          }
        ]
      };

      myChart.setOption(option);
    });
  }

  xui.tbLoad('vendorPlantDiv', drawChart);
})();
