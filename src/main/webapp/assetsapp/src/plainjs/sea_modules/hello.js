define(function(require, exports, module) {
  const sayHello = () => {
    return 'sayHello.';
  };

  const bye = () => {
    return 'another function.';
  };

  module.exports = {
    sayHello,
    bye
  };
});
