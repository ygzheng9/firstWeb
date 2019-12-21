import { Controller } from 'stimulus';

import axios from 'axios';

import zzdom from '../zzdom';

export default class extends Controller {
  static targets = ['result'];

  connect() {
    const { dateFormat } = zzdom;
    const { start, end } = zzdom.genRange();

    bulmaCalendar.attach('[type="date"]', {
      type: 'date',
      isRange: true,
      dateFormat: dateFormat,
      lang: 'zh_cn',
      startDate: start,
      endDate: end
    });

    this.doSearch();
  }

  doSearch() {
    let data = zzdom.getFormData('workdaySearch');
    const r = zzdom.splitRange(data.bizDate);
    data = { ...data, ...r };

    const url = '/pages/workday/doSearch';

    axios
      .post(url, data)
      .then(res => res.data)
      .then(res => {
        zzdom.setHtml(res)(this.resultTarget);
      })
      .catch(function(error) {
        console.log(error);
        layer.msg('出问题了.....', { icon: 5, time: 1500 });
      });
  }

  search(evt) {
    evt.preventDefault();

    this.doSearch();
  }
}
