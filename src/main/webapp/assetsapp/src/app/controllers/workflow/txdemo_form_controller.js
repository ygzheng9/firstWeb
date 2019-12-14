import { Controller } from 'stimulus';

import axios from 'axios';

import zzdom from '../zzdom';

export default class extends Controller {
  connect() {
    zzdom.initSelectOptions('txFormDemo');
  }

  submit(evt) {
    evt.preventDefault();

    const cfg = [
      {
        id: '#saveForm',
        url: '/pages/workflow/txDemo/saveEntry',
        target: '/pages/workflow/txDemo/list'
      },
      {
        id: '#submitForm',
        url: '/pages/workflow/txDemo/submit',
        target: '/pages/workflow/engine/list'
      }
    ];
    const self = evt.currentTarget;
    const parma = self.dataset['params'];
    const { url, target } = cfg[parma];
    // console.log(url, target);

    let data = zzdom.getFormData('txFormDemo');
    //   console.log(data);

    axios
      .post(url, data)
      .then(_res => {
        Turbolinks.visit(target);
      })
      .catch(function(error) {
        console.log(error);
      });
  }
}
