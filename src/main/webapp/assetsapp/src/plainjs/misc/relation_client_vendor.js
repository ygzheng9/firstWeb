(() => {
  let myChart = echarts.init(document.getElementById('myChartDiv'));

  const url = '/pages/relation/clientVendorData';
  $.get(url, res => {
    const { nodes, links } = res.data;

    const categories = [
      { name: 'vendor', label: '供应商' },
      { name: 'client', label: '客户' }
    ];

    nodes.forEach(node => {
      if (node.category === 'vendor') {
        node.symbolSize = Math.abs(node.amt / 10000000.0);
      } else if (node.category === 'client') {
        node.symbolSize = Math.abs(node.amt / 10000000.0);
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
            repulsion: 200,
            gravity: 0.3,
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
})();
