import { Controller } from 'stimulus';

import $ from 'jquery';

export default class extends Controller {
  connect() {
    this.startup();
  }

  startup() {
    // 拦截点击
    $('.content-tab').on('click', 'a.drillDown', function() {
      const self = $(this);

      const url = self.attr('data-href');
      const k = self.attr('data-key');

      const param = {
        matAmtByOrderID: 'tab2',
        itemByOrderMatID: 'tab3'
      };

      $(`#${k}`).load(url, () => {
        openTab(param[k]);
      });
    });

    // tab 切换
    const tabConfig = {
      tab1: 'byVendor',
      tab2: 'byMat',
      tab3: 'byInbound'
    };

    function openTab(tabid) {
      // 所有的 tab 都不选中
      $('.tab').removeClass('is-active');

      // 隐藏所有的 content
      $('.content-tab').css('display', 'none');

      // 当前的 tab 选中，对应的 content 显示
      $(`#${tabid}`).addClass('is-active');

      const tabContent = tabConfig[tabid];

      $(`#${tabContent}`).css('display', 'block');
    }

    $('.tab').on('click', function() {
      const self = $(this);
      const tabid = self.attr('id');

      openTab(tabid);
    });
  }
}
