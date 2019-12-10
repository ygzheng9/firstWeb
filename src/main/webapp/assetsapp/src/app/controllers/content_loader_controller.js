import { Controller } from 'stimulus';

import axios from 'axios';

export default class extends Controller {
  connect() {
    this.load();
  }

  load() {
    axios(this.data.get('url'))
      .then(response => {
        console.log(response);
        return response.data;
      })
      .then(html => {
        this.element.innerHTML = html;
      });
  }
}
