import 'babel-polyfill';
import '!style-loader!css-loader!sass-loader!./css/style-base.scss';
import '!style-loader!css-loader!sass-loader!./css/style.scss';
import '!style-loader!css-loader!sass-loader!./css/ss.scss';
import FessMessages from './fess-messages.js';
import FessView from './view.js';
import FessController from './controller.js';
import FessModel from './model.js';

(function() {
  var fessMessages = new FessMessages();
  var fessView = new FessView(fessMessages);
  var fessModel = new FessModel();
  var fessController = new FessController(fessView, fessModel);
  fessController.start();
})();
