import { Controller } from 'stimulus';

export default class extends Controller {
  static targets = ['name', 'balabala', 'slide'];

  connect() {
    // console.log('Stimulus connected!');
  }

  initialize() {
    // const param = this.element.getAttribute('data-hello-index');
    if (!this.data.has('index')) {
      return;
    }

    const index = parseInt(this.data.get('index'));
    this.showSlide(index);
    // console.log('Stimulus initialized!');
  }

  greet() {
    // console.log('Hello, Stimulus!', this.element);

    const element = this.nameTarget;
    console.log(this);

    const name = element.value;
    console.log(`Hello, ${name}!`);
  }

  copy(event) {
    event.preventDefault();

    this.balabalaTarget.select();
    document.execCommand('copy');
  }

  showSlide(index) {
    this.index = index;
    this.slideTargets.forEach((el, i) => {
      el.classList.toggle('slide--current', i === index);
    });
  }

  previous() {
    this.showSlide(this.index - 1);
  }

  next() {
    this.showSlide(this.index + 1);
  }
}
