(() => {
  $(document).on('click', '#tblList .btnApprove', function() {
    const self = $(this);
    const params = self.attr('data-params');
    const url = `/pages/workflow/engine/doApprove?id=${params}`;

    axios.post(url).then(function() {
      window.location.reload();
    });

    return false;
  });

  $(document).on('click', '#tblList .btnReject', function() {
    const self = $(this);
    const params = self.attr('data-params');
    const url = `/pages/workflow/engine/doReject?id=${params}`;

    axios.post(url).then(function() {
      window.location.reload();
    });

    return false;
  });
})();
