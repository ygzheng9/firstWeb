import { Controller } from 'stimulus';

import zzdom from '../zzdom';
import { map } from 'ramda';

export default class extends Controller {
  connect() {
    // console.log('project list connected....');
  }

  // 这是个弹出的窗口，每次操作都把当前选中的信息，写入到宿主页面的 dom 中
  onSelect(evt) {
    evt.preventDefault();
    const self = evt.currentTarget;

    // 所有行取消选中
    const rows = this.element.querySelectorAll('tr');
    map(zzdom.removeClass('is-selected'))(rows);

    // 当前行选中
    zzdom.addClass('is-selected')(self);

    // 选中行写入 宿主页面 dom。注意：projectList-outputs 是宿主页面中的 dom
    const t = document.getElementById('projectList-outputs');
    const s = self.getAttribute('data-outputs');

    // zzdom.setHtml(s)(t);

    t.innerHTML = s;
  }
}
