import { Controller } from 'stimulus';

import axios from 'axios';
import $ from 'jquery';

export default class extends Controller {
  static targets = ['clientPlantHeat', 'bomList'];

  connect() {
    this.startup();
  }

  startup() {
    xui.initSelectOptions('#txFormDemo');

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

    function clickButton(idx) {
      let data = xui.getFormData('#txFormDemo');
      // console.log(data);

      const { url, target } = cfg[idx];
      axios
        .post(url, data)
        .then(function(response) {
          // console.log(response);
          // window.location.href = target;
          Turbolinks.visit(target);
        })
        .catch(function(error) {
          console.log(error);
        });
    }

    $('#saveForm').on('click', function(e) {
      e.preventDefault();

      clickButton(0);

      return false;
    });

    $('#submitForm').on('click', function(e) {
      e.preventDefault();

      clickButton(1);

      return false;
    });
  }
}
