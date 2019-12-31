import { Controller } from 'stimulus';

import axios from 'axios';

import Swal from 'sweetalert2';

import zzdom from '../zzdom';

export default class extends Controller {
  static targets = ['simpleModal', 'projectSelections'];

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
      .then(res => {
        console.log(res.headers);
        return res.data;
      })
      .then(_res => {
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

  // 弹出新页面，并把选中结果带回到当前页面
  // html -> js: 实现后台 java，前台的 html，js 之间信息互通；
  // 1. 通过 data-xxx 属性传递；而 data-xx 是 enjoy render 出来的；
  // 2. render 是在 action 中，而 action 是可以从 request 中获取数据的；
  // 3. request 可以是 html 中的 querystring，或在 js 发起；
  // 4.1 html 中的 querystring 是前一个 action render 的；
  // 4.2 js 通过 getAttribute 可以读取 html 中 data-xx 的数据；
  projectList(evt) {
    evt.preventDefault();

    // 读取 html 中的参数，通过 request 发给后台 java
    const pcode = document.getElementById('projectCode').value;
    const url = `/pages/workday/projectList?s=${pcode}`;

    axios
      .get(url)
      .then(res => res.data)
      .then(res => {
        // 后台直接返回 html，前台通过 layer 打开新窗口
        layer.open({
          //   type: 1,
          //   title: '在线调试',
          area: ['800px', '400px'],
          content: res,
          yes: function(index, layero) {
            // layer "确认" 关闭，读取窗口内操作的结果（写入了 hidden）
            const s = document.getElementById('projectList-outputs').innerHTML;
            const a = JSON.parse(s);
            console.log(a);

            // 设置当前页面的属性
            document.getElementById('projectCode').value = a.value;

            // 关闭 layer
            layer.close(index);
          }
        });
      });
  }

  openModal(evt) {
    evt.preventDefault();

    //  最近简单的 modal
    // Swal.fire({
    //   icon: 'error',
    //   title: 'Oops...',
    //   text: 'Something went wrong!',
    //   footer: '<a href>Why do I have this issue?</a>'
    // });

    // 有按钮的
    const swalWithBulmaButtons = Swal.mixin({
      customClass: {
        confirmButton: ' button is-success',
        cancelButton: 'button is-danger'
      },
      buttonsStyling: false
    });

    swalWithBulmaButtons
      .fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'No, cancel!',
        reverseButtons: true
      })
      .then(result => {
        if (result.value) {
          swalWithBulmaButtons.fire(
            'Deleted!',
            'Your file has been deleted.',
            'success'
          );
        } else if (
          /* Read more about handling dismissals below */
          result.dismiss === Swal.DismissReason.cancel
        ) {
          swalWithBulmaButtons.fire(
            'Cancelled',
            'Your imaginary file is safe :)',
            'error'
          );
        }
      });
  }

  bulmaModal(evt) {
    evt.preventDefault();

    // 读取 html 中的参数，通过 request 发给后台 java
    const pcode = document.getElementById('projectCode').value;
    const url = `/pages/workday/projectList?s=${pcode}`;

    axios.get(url).then(res => {
      console.log(res.headers);

      // return res.data;

      const { data } = res;

      console.log(data.count);

      this.projectSelectionsTarget.innerHTML = data.html;

      this.simpleModalTarget.classList.add('is-active');
    });
  }

  closeModal(evt) {
    evt.preventDefault();
    this.simpleModalTarget.classList.remove('is-active');
  }
}
