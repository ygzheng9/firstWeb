// base 是 module 的 url 地址，app 是在 use 时使用的，都是 url，不是本地文件目录
// seajs 没有预处理的过程，所有文件都是 url 地址；和 layerui 是一样的逻辑；
seajs.config({
  base: '/assets/js/sea_modules/',
  alias: {
    jquery: '/assets/lib/jquery-3.4.1.min.js'
  },
  paths: {
    app: '/assets/js'
  }
});
