//config的设置是全局的
layui
  .config({
    // 这里是 url，不是本地路径
    base: '/assets/js/lay_modules/'
  })
  .extend({
    // 设定模块别名，在 use 时使用
    // key 和 模块文件 exports 的名字保持一致
    okTab: 'okmodules/okTab',
    countUp: 'okmodules/countUp',
    okUtils: 'okmodules/okUtils',
    okGVerify: 'okmodules/okGVerify',
    qrcode: 'okmodules/qrcode',
    jQqrcode: 'okmodules/jQqrcode',
    okAddlink: 'okmodules/okAddlink',
    okLayer: 'okmodules/okLayer',
    okMock: 'okmodules/okMock',

    zzUtils: 'zz/utils'
  });
