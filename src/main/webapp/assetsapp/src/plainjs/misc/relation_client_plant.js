(() => {
  let myChart = echarts.init(document.getElementById('myChartDiv'));

  const url = '/pages/relation/clientPlantData';
  $.get(url, res => {
    const { nodes, links } = res.data;

    const categories = [
      { name: 'client', label: '客户' },
      { name: 'plant', label: '工厂' },
      { name: 'project', label: '项目' }
    ];

    nodes.forEach(node => {
      node.symbolSize = node.value * 10;

      if (node.symbolSize < 3) {
        node.symbolSize = 3;
      }

      node.label = {
        normal: {
          //   show: node.symbolSize > 20
        }
      };
    });

    let option = {
      //   backgroundColor: '#FFFFFF',
      animationDuration: 2000,
      animationEasingUpdate: 'quinticInOut',
      tooltip: {
        formatter: '{b}'
      },
      legend: {
        left: 'right',
        data: categories.map(function(a) {
          return a.name;
        }),
        formatter: function(name) {
          const i = _.find(categories, i => i.name === name);
          if (i !== undefined) {
            return i.label;
          }
          return '';
        }
      },
      series: [
        {
          type: 'graph',

          categories: categories,

          layout: 'circular',
          circular: {
            rotateLabel: true
          },
          symbolSize: 10,
          roam: true,
          focusNodeAdjacency: true,
          emphasis: {
            lineStyle: {
              width: 3
            }
          },
          label: {
            normal: {
              show: true,
              position: 'right',
              formatter: '{b}',
              fontSize: 10,
              fontStyle: '300'
            }
          },
          lineStyle: {
            normal: {
              color: 'source',
              opacity: 0.9,
              width: 0.5,
              curveness: 0.3
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
