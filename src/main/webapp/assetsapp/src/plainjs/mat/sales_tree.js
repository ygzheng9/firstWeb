(() => {
  const myChart = echarts.init(document.getElementById('salesTree'));
  const year = '2016';
  const url = `/pages/sales/regionData?year=${year}`;

  const toTree = source => {
    const result = source.map(s => {
      const region = s[2];
      const total = s[4];
      //   return { name: region, value: total, subCount: s[3], subRatio: s[5] };
      return { name: region, value: [total, s[3], s[5]] };
    });

    // console.log('toTree: ', result);
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
    // console.log(res);
    const { dims, source, total } = res;

    const treeData = toTree(source);
    // console.log(treeData);

    const range = getRange(source, byWhat);
    // console.log(range);

    const option = {
      title: {
        top: 'top',
        left: 'center',
        text: '销售分析',
        subtext: 'By Region'
      },
      //   color: ['#3398DB'],
      tooltip: {
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
  });
})();
