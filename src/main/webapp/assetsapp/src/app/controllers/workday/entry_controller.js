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

  projectList(evt) {
    evt.preventDefault();

    const pcode = document.getElementById('projectCode').value;
    const url = `/pages/workday/projectList?s=${pcode}`;

    axios
      .get(url)
      .then(res => res.data)
      .then(res => {
        layer.open({
          //   type: 1,
          //   title: '在线调试',
          area: ['800px', '400px'],
          content: res,
          yes: function(index, layero) {
            const s = document.getElementById('projectList-outputs').innerHTML;
            const a = JSON.parse(s);
            console.log(a);

            document.getElementById('projectCode').value = a.value;

            layer.close(index);
          }
        });
      });
  }
}
