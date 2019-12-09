'use strict';
layui.use(['okUtils', 'countUp'], function() {
  var countUp = layui.countUp;
  var okUtils = layui.okUtils;
  var $ = layui.jquery;

  // 顶部四个只的动态效果
  function initMediaCont() {
    var elem_nums = $('.media-content .count-up');
    elem_nums.each(function(i, j) {
      const a = $(j).text();

      !new countUp({
        target: j,
        endVal: a
      }).start();
    });
  }

  // 顶部四个显示值的曲线图
  function dataTrendOption(color) {
    color = color || '#00c292';
    return {
      color: color,
      toolbox: { show: false, feature: { saveAsImage: {} } },
      grid: {
        left: '-1%',
        right: '0',
        bottom: '0',
        top: '5px',
        containLabel: false
      },
      xAxis: [
        {
          type: 'category',
          boundaryGap: false,
          splitLine: { show: false },
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        }
      ],
      yAxis: [{ type: 'value', splitLine: { show: false } }],
      series: [
        {
          name: 'dummy',
          type: 'line',
          stack: '总量',
          smooth: true,
          symbol: 'none',
          clickable: false,
          areaStyle: {},
          data: [
            randomData(),
            randomData(),
            randomData(),
            randomData(),
            randomData(),
            randomData(),
            randomData()
          ]
        }
      ]
    };
  }

  function randomData() {
    return Math.round(Math.random() * 500);
  }

  /**
   * dummy 图表
   */
  function initDataTrendChart() {
    // 收入
    var echIncome = echarts.init($('#echIncome')[0]);
    // 商品
    var echGoods = echarts.init($('#echGoods')[0]);
    // 博客
    var echBlogs = echarts.init($('#echBlogs')[0]);
    // 用户
    var echUser = echarts.init($('#echUser')[0]);
    echIncome.setOption(dataTrendOption('#00c292'));
    echGoods.setOption(dataTrendOption('#ab8ce4'));
    echBlogs.setOption(dataTrendOption('#03a9f3'));
    echUser.setOption(dataTrendOption('#fb9678'));
    okUtils.echartsResize([echIncome, echGoods, echBlogs, echUser]);
  }

  function userSourceWeekChart() {
    // 入库 和 bom 料号 匹配的数量，金额
    const domid = $('#matchedMatData');
    const raw = domid.attr('data-matchedMat');
    const d = JSON.parse(raw);

    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        x: 'left',
        data: ['匹配金额', '未匹配金额', '匹配物料', '未匹配物料']
      },
      grid: {
        bottom: '5%'
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
          data: [
            { value: d.matAmt, name: '匹配金额', selected: true },
            { value: d.totalAmt - d.matAmt, name: '未匹配金额' }
          ]
        },
        {
          name: '料号占比',
          type: 'pie',
          radius: ['40%', '55%'],
          label: {
            normal: {
              formatter: '{a|{a}}{abg|}\n{hr|}\n  {b|{b}：}{c}  {per|{d}%}  ',
              backgroundColor: '#eee',
              borderColor: '#aaa',
              borderWidth: 1,
              borderRadius: 4,
              // shadowBlur:3,
              // shadowOffsetX: 2,
              // shadowOffsetY: 2,
              // shadowColor: '#999',
              // padding: [0, 7],
              rich: {
                a: {
                  color: '#999',
                  lineHeight: 22,
                  align: 'center'
                },
                // abg: {
                //     backgroundColor: '#333',
                //     width: '100%',
                //     align: 'right',
                //     height: 22,
                //     borderRadius: [4, 4, 0, 0]
                // },
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
          data: [
            { value: d.matCount, name: '匹配物料' },
            { value: d.totalCount - d.matCount, name: '未匹配物料' }
          ]
        }
      ]
    };

    const matchedMatChart = echarts.init(
      document.getElementById('matchedMatChart')
    );

    matchedMatChart.setOption(option, true);
  }

  function matSourceRatio() {
    // 物料来源数量的金额明细，

    $.get('/pages/inbound/matSourceRatio', res => {
      const items = res.items.map(i => ({
        countRatio: i.countRatio.toFixed(2),
        amtRatio: i.amtRatio.toFixed(2)
      }));

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        legend: {
          data: ['1', '2', '3']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value'
        },
        yAxis: {
          type: 'category',
          data: ['料号数量%', '采购金额%']
        },
        series: [
          {
            name: '1',
            type: 'bar',
            stack: '总量',
            label: {
              normal: {
                show: true,
                position: 'insideRight'
              }
            },
            data: [items[0].countRatio, items[0].amtRatio]
          },
          {
            name: '2',
            type: 'bar',
            stack: '总量',
            label: {
              normal: {
                show: true,
                position: 'insideRight'
              }
            },
            data: [items[1].countRatio, items[1].amtRatio]
          },
          {
            name: '3',
            type: 'bar',
            stack: '总量',
            label: {
              normal: {
                show: true,
                position: 'insideRight'
              }
            },
            data: [items[2].countRatio, items[2].amtRatio]
          }
        ]
      };

      const myChart = echarts.init(document.getElementById('matSourceChart'));

      myChart.setOption(option, true);

      myChart.on('click', 'series', params => {
        const url = '/pages/inbound/matMultiSource';
        // window.location.href = url;
        Turbolinks.visit(url);
      });
    });
  }

  initMediaCont();

  initDataTrendChart();
  userSourceWeekChart();
  matSourceRatio();
});
