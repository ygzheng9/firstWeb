$(function() {
  var tableIns;
  layui.use('table', function() {
    var table = layui.table;

    const avg = $('#avgRate').data('avg');
    console.log(avg);

    tableIns = table.render({
      skin: 'line', //行边框风格
      even: true, //开启隔行背景
      size: 'sm', //小尺寸的表格
      elem: '#projectList',
      url: '/pages/mat/getProjectReuseData',
      limit: 10,
      page: false, //开启分页
      cols: [
        [
          { field: 'client', title: '客户', width: '10%', sort: true },
          { field: 'project', title: '项目', width: '10%', sort: true },
          {
            field: 'bomCount',
            title: 'BOM 数量',
            width: 120,
            sort: true,
            align: 'right'
          },
          {
            field: 'partCount',
            title: '总料号',
            width: 120,
            sort: true,
            align: 'right'
          },
          {
            field: 'reusePartCount',
            title: '复用料号',
            width: 120,
            sort: true,
            align: 'right',
            templet: function(d) {
              return (
                '<span><a href="pages/mat/getProjectMatList?p=' +
                d.project +
                '>' +
                d.reusePartCount +
                '</a></span>'
              );
            }
          },
          {
            field: 'reuseRate',
            title: '复用率%',
            // width: 120,
            sort: true,
            align: 'right',
            templet: function(d) {
              if (d.reuseRate <= avg) {
                return '<span style="color: red;">' + d.reuseRate + '</span>';
              } else {
                return '<span>' + d.reuseRate + '</span>';
              }
            }
          }
        ]
      ],
      parseData: function(res) {
        return {
          code: 0,
          msg: '',
          // "count": res.totalRow,
          data: res.items
        };
      }
    });
  });
});
