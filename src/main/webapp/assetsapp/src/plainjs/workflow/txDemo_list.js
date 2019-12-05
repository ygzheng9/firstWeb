layui.use(['zzUtils'], () => {
  const { zzUtils } = layui;

  $('#saveForm').on('click', function(e) {
    e.preventDefault();

    let data = zzUtils.getFormData('#txFormDemo');
    // console.log(data);

    const url = '/pages/workflow/txDemo/createEntry';
    axios
      .post(url, data)
      .then(function(response) {
        console.log(response);
      })
      .catch(function(error) {
        console.log(error);
      });

    return false;
  });
});
