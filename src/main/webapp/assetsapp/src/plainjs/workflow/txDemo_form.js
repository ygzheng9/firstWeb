layui.use(['zzUtils'], () => {
  const { zzUtils } = layui;

  zzUtils.initSelectOptions('#txFormDemo');

  $('#saveForm').on('click', function(e) {
    e.preventDefault();

    let data = zzUtils.getFormData('#txFormDemo');
    // console.log(data);

    const url = '/pages/workflow/txDemo/saveEntry';
    axios
      .post(url, data)
      .then(function(response) {
        // console.log(response);
        window.location.href = '/pages/workflow/txDemo/list';
      })
      .catch(function(error) {
        console.log(error);
      });

    return false;
  });
});
