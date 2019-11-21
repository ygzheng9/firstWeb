function matSourceRatio() {
  // 订单+物料 入库明细

  const order = $('#paramOrder').attr('data-param');
  const mat = $('#paramMat').attr('data-param');

  const url = `/pages/inbound/itemByOrderMatData?o=${order}&m=${mat}`;

  $.get(url, res => {
    const { items } = res;

    const xs = items.map(i => i.ibDate);
    const vals = items.map(i => i.totalQty);
    const unitCost = items.map(i => i.unitCost);

    const option = {
      title: { text: `${mat} 入库记录`, top: 'top', left: 'center' },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          // 坐标轴指示器，坐标轴触发有效
          type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: xs
      },
      yAxis: [
        {
          type: 'value',
          name: '入库量',
          position: 'left'
        },
        {
          type: 'value',
          name: '单位成本',
          position: 'right'
        }
      ],
      series: [
        {
          name: '入库量',
          data: vals,
          type: 'line',
          yAxisIndex: 0
        },
        {
          name: '单位成本',
          data: unitCost,
          type: 'line',
          yAxisIndex: 1
        }
      ]
    };

    const myChart = echarts.init(document.getElementById('myChart'));
    myChart.setOption(option, true);
  });
}

matSourceRatio();
