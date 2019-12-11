import { Controller } from 'stimulus';

import $ from 'jquery';

export default class extends Controller {
  static targets = ['navMenu', 'menu'];

  connect() {
    this.highligtMenu();
  }

  // 当屏幕变窄时，顶部菜单会隐藏，同时在右上角有按钮可点击，显示下拉菜单
  click() {
    this.navMenuTarget.classList.toggle('is-active');
    this.menuTarget.classList.toggle('is-active');
  }

  // 根据 后台传入的 actionKey，设置页面中菜单的选中状态
  highligtMenu() {
    // actionKeyID 是后台传入的，记录了 actionKey
    const selectedMenu = this.data.get('actionKey');

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
  }
}
