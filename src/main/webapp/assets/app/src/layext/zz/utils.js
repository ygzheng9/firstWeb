layui.define(exports => {
  var output = {
    getUniq: (list, name) => {
      let a = list.map(i => i[name]);
      a = _.uniq(a);
      return a;
    },

    setHtml: (url, domid) => {
      $.get(url, res => {
        document.getElementById(domid).innerHTML = res;
      });
    },

    echo: msg => {
      console.log(`from zz/utils: ${msg}`);
    }
  };

  exports('zzUtils', output);
});
