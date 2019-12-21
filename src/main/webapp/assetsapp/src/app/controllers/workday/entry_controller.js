import { Controller } from 'stimulus';

import axios from 'axios';

import zzdom from '../zzdom';

export default class extends Controller {
  connect() {
    bulmaCalendar.attach('[type="date"]', {
      type: 'date',
      displayMode: 'default',
      dateFormat: 'YYYY-MM-DD',
      lang: 'zh_cn',
      startDate: new Date()
    });
  }

  saveEntry(evt) {
    evt.preventDefault();

    let data = zzdom.getFormData('workdayEntry');
    console.log(data);

    const url = '/pages/workday/saveEntry';

    axios
      .post(url, data)
      .then(res => res.data)
      .then(_res => {
        console.log(_res);

        const { state, msg, id } = _res;
        if (state !== 'ok') {
          layer.msg(msg);
        } else {
          //   layer.msg('保存成功');

          layer.msg(`保存成功`, function() {
            const target = `/pages/workday/openEntry?id=${id}`;
            zzdom.visit(target);
          });
        }
      })
      .catch(function(error) {
        console.log(error);
        layer.msg('出问题了.....', { icon: 5, time: 1500 });
      });
  }

  newEntry(evt) {
    evt.preventDefault();

    const target = '/pages/workday/newEntry';
    zzdom.visit(target);
  }

  cancel(evt) {
    evt.preventDefault();

    const target = '/pages/workday/search';
    zzdom.visit(target);
  }
}
