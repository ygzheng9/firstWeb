"use strict";

layui.use(['zzUtils'], function () {
  var _layui = layui,
      zzUtils = _layui.zzUtils; //   tools.echo('called from mat_reuse1');

  var myChart = echarts.init(document.getElementById('reuse1'));
  $.get('/pages/mat/reuseByBomData', function (res) {
    // console.log(res);
    var items = res.items;
    var levels = items.map(function (i) {
      return i.repeatedCnt;
    });
    var levelSizeRaw = items.map(function (i) {
      return i.size;
    });
    var levelSize = levelSizeRaw.map(function (i) {
      var j = i;

      if (i === 1) {
        j = 1.1;
      }

      return Math.log10(j);
    });
    var option = {
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
      xAxis: [{
        type: 'category',
        name: '复用次数',
        data: levels,
        axisTick: {
          alignWithLabel: true
        },
        nameGap: 25,
        nameLocation: 'middle'
      }],
      yAxis: [{
        type: 'value',
        name: '料号数量(log10)',
        nameLocation: 'middle',
        nameGap: 25,
        nameRotate: 90
      }],
      series: [{
        type: 'bar',
        name: '料号数量',
        barWidth: '60%',
        data: levelSize,
        tooltip: {
          trigger: 'item',
          formatter: function formatter(param) {
            return ['使用次数：' + param.name + '<br/>', '料号数量：' + levelSizeRaw[param.dataIndex]].join('');
          }
        }
      }]
    };
    myChart.setOption(option, true);
    myChart.on('click', 'series', onClickChart);

    function onClickChart(params) {
      var count = params.name;
      var url = '/pages/mat/getMatByReuseCount?count=' + count;
      zzUtils.setHtml(url, 'matlist');
    }
  });
});
//# sourceMappingURL=mat_reuse1.js.map
