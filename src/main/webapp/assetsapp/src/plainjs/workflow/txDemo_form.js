(() => {
  function init() {
    layui.use(['zzUtils'], () => {
      const { zzUtils } = layui;

      zzUtils.initSelectOptions('#txFormDemo');

      const cfg = [
        {
          id: '#saveForm',
          url: '/pages/workflow/txDemo/saveEntry',
          target: '/pages/workflow/txDemo/list'
        },
        {
          id: '#submitForm',
          url: '/pages/workflow/txDemo/submit',
          target: '/pages/workflow/engine/list'
        }
      ];

      function clickButton(idx) {
        let data = zzUtils.getFormData('#txFormDemo');
        // console.log(data);

        const { url, target } = cfg[idx];
        axios
          .post(url, data)
          .then(function(response) {
            // console.log(response);
            // window.location.href = target;
            Turbolinks.visit(target);
          })
          .catch(function(error) {
            console.log(error);
          });
      }

      $('#saveForm').on('click', function(e) {
        e.preventDefault();

        clickButton(0);

        return false;
      });

      $('#submitForm').on('click', function(e) {
        e.preventDefault();

        clickButton(1);

        return false;
      });
    });
  }

  // 如果只是 绑定事件，可以放到 load 中，但是其实不需要加入到 load 事件中，使用 delegation 做一次绑定就好了；
  xui.tbAddLoad('txFormDemo', init);
})();
