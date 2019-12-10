layui.define(exports => {
  var inner = {
    getUniq: (list, name) => {
      let a = list.map(i => i[name]);
      a = _.uniq(a);
      return a;
    },

    setHtml: (url, domid) => {
      $.get(url, res => {
        // 直接赋值
        document.getElementById(domid).innerHTML = res;

        // jquery 是函数
        // $(domid).text(res);
      });
    },

    /**
     * 根据form表单的id获取表单下所有可提交的表单数据，封装成数组对象
     */
    getFormData: formId => {
      var data = {};
      var results = $(formId).serializeArray();
      $.each(results, function(index, item) {
        //文本表单的值不为空才处理
        if (item.value && $.trim(item.value) !== '') {
          if (!data[item.name]) {
            data[item.name] = item.value;
          } else {
            //name属性相同的表单，以 空格 拼接
            data[item.name] = data[item.name] + ' ' + item.value;
          }
        }
      });
      //console.log(data);
      return data;
    },

    // select 后面紧跟一个 hidden，在 hidden 中有 select 的值
    initSelectOptions: formID => {
      $(`${formID} select`).each(function() {
        const self = $(this);
        const v = self.next().val();
        if (v === undefined || v.length === 0) {
          return;
        }
        self.val(v);
      });
    },

    echo: msg => {
      console.log(`from zz/utils: ${msg}`);
    }
  };

  exports('zzUtils', inner);
});
