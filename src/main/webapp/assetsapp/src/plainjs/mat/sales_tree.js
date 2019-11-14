(() => {
  const myChart = echarts.init(document.getElementById('salesTree'));
  const year = '2016';
  const url = `/pages/sales/regionData?year=${year}`;

  const cityChart = echarts.init(document.getElementById('citySales'));

  const toTree = source => {
    const result = source.map(s => {
      const region = s[2];
      const total = s[4];
      //   return { name: region, value: total, subCount: s[3], subRatio: s[5] };
      return { name: region, value: [total, s[3], s[5]] };
    });

    return result;
  };

  const getRange = (source, by) => {
    // 需要和 source 的列做对应
    const idx = by === bySubCount ? 3 : 5;
    const items = source.map(i => i[idx]);
    return [_.min(items), _.max(items) + 1];
  };

  const bySubCount = 1;
  const byRatio = 2;

  let byWhat = byRatio;

  $.get(url, res => {
    const { dims, source, total } = res;

    // 如果 ratio > 15，则设置成 15
    const source2 = source.map(i => {
      if (i[5] > 15) {
        i[5] = 15;
      }

      return i;
    });

    const treeData = toTree(source2);

    const range = getRange(source2, byWhat);

    const option = {
      title: {
        top: 'top',
        left: 'center',
        text: '区域销售分析'
      },
      tooltip: {
        // 只有这里设置了，才能显示 tooltip
        trigger: 'axis',
        axisPointer: {
          // 坐标轴指示器，坐标轴触发有效
          type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
      },
      series: [
        {
          type: 'treemap',
          name: '全国',
          data: treeData,
          // 距离顶部的距离
          //   top: 40,
          // 显示的字颜色
          label: {
            color: '#000',
            // fontWeight: 'bold'
            fontSize: 12
          },
          tooltip: {
            trigger: 'item',
            formatter: function(param) {
              const { name, value } = param.data;
              return [
                '区域: ' + name + '<br/>',
                '销量：' + value[0] + '<br/>',
                '经销商数量：' + value[1] + '<br/>',
                '落差比: ' + value[2]
              ].join('');
            }
          }
        }
      ],
      visualMap: [
        {
          type: 'continuous', //连续型
          show: false,
          dimension: byWhat, //对应的数组维度
          min: range[0],
          max: range[1]
        }
      ]
    };

    myChart.setOption(option, true);

    myChart.on('click', 'series', params => {
      if (params.data === undefined) {
        return;
      }

      const { name } = params.data;

      const url = `/pages/sales/cityData?year=${2016}&region=${name}`;

      $.get(url, res => {
        const { dims, source } = res;
        const option = {
          title: {
            top: 'top',
            left: 'center',
            text: `区域销量：${name}`
          },
          dataset: {
            dimension: dims,
            source: source
          },
          xAxis: {
            type: 'value',
            position: 'top'
          },
          yAxis: {
            type: 'category',
            axisLabel: {
              // 显示所有的 label
              interval: 0,
              fontSize: 8
            }
          },
          series: [
            {
              encode: { x: 'quantity', y: 'city' },
              type: 'bar',
              itemStyle: {
                // 柱状图颜色
                color: '#4ad2ff'
              }
            }
          ]
        };

        cityChart.setOption(option);
      });
    });
  });
})();
