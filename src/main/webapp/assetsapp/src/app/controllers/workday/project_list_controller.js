import { Controller } from 'stimulus';

import zzdom from '../zzdom';
import { map } from 'ramda';

export default class extends Controller {
  connect() {
    console.log('project list connected....');
  }

  onSelect(evt) {
    evt.preventDefault();
    const self = evt.currentTarget;

    // 所有行取消选中
    const rows = zzdom.bySelector('#projectList tr');
    map(zzdom.removeClass('is-selected'))(rows);

    // 当前行选中
    zzdom.addClass('is-selected')(self);

    // 当前行写入页面
    const t = zzdom.byId('projectList-outputs');
    const s = self.getAttribute('data-outputs');
    zzdom.setHtml(s)(t);
  }
}
