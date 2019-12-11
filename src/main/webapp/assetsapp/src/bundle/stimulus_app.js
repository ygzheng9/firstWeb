import { Application } from 'stimulus';
import { definitionsFromContext } from 'stimulus/webpack-helpers';

var Turbolinks = require('turbolinks');
Turbolinks.start();
Turbolinks.setProgressBarDelay(100);

const application = Application.start();
const context = require.context('../app/controllers', true, /\.js$/);
application.load(definitionsFromContext(context));
