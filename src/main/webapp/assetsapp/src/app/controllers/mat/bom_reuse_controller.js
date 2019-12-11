import { Controller } from 'stimulus';

export default class extends Controller {
  connect() {
    this.startup();
  }

  startup() {
    var tableIns;
    layui.use('table', function() {
      var table = layui.table;

      tableIns = table.render({
        skin: 'line', //行边框风格
        even: true, //开启隔行背景
        size: 'sm', //小尺寸的表格
        elem: '#bomList',
        url: '/pages/mat/getBomReuseData',
        limit: 10,
        page: false, //开启分页
        cols: [
          [
            { field: 'client', title: '客户', width: '10%', sort: true },
            { field: 'project', title: '项目', width: '10%', sort: true },
            { field: 'bomID', title: 'BOM', sort: true },
            {
              field: 'partCount',
              title: '总料号',
              width: 120,
              sort: true,
              align: 'right'
            },
            {
              field: 'repeatCnt',
              title: '复用料号',
              width: 120,
              sort: true,
              align: 'right'
            },
            {
              field: 'reuseRate',
              title: '复用率%',
              width: 120,
              sort: true,
              align: 'right',
              templet: '<div>{{ (d.reuseRate * 100).toFixed(2) }}</div>'
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
  }
}
