(function() {
  const selectedMenu = $('#selectedMenu').attr('data-menu');

  //   顶部菜单
  const navSelector = `.navbar-dropdown a[href='${selectedMenu}']`;
  const navs = $(navSelector);
  if (navs.length > 0) {
    navs.addClass('is-active');
  }

  //    左侧菜单
  const menuSelector = `.menu-list a[href='${selectedMenu}']`;
  const menus = $(menuSelector);
  if (menus.length > 0) {
    menus.addClass('is-active');
  }
})();
