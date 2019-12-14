import { Controller } from 'stimulus';

import axios from 'axios';

import dom from '../zzdom';

import { forEach } from 'ramda';

export default class extends Controller {
  connect() {}

  openTab(tabid) {
    // 所有的 tab 都不选中
    forEach(dom.removeClass('is-active'), document.querySelectorAll('.tab'));

    // 隐藏所有的 content
    forEach(dom.setDisplay('none'), document.querySelectorAll('.content-tab'));

    // 当前的 tab 选中，对应的 content 显示
    dom.addClass('is-active')(document.getElementById(tabid));

    // tab 切换
    const tabConfig = {
      tab1: 'content-tab1',
      tab2: 'content-tab2',
      tab3: 'content-tab3'
    };

    const tabContent = tabConfig[tabid];
    dom.setDisplay('block')(document.getElementById(tabContent));
  }

  clicktab(evt) {
    const self = evt.currentTarget;
    const tabid = self.getAttribute('id');

    this.openTab(tabid);
  }

  drilldown(evt) {
    const self = evt.currentTarget;

    const url = self.dataset['href'];
    const k = self.dataset['key'];

    const param = {
      matVendorsID: 'tab2',
      plantItemsID: 'tab3'
    };

    axios
      .get(url)
      .then(res => res.data)
      .then(html => {
        dom.setHtml(html)(document.getElementById(k));

        this.openTab(param[k]);
      });
  }
}
