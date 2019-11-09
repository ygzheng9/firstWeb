//config的设置是全局的
layui
  .config({
    base: '/assets/js/layext/' //假设这是你存放拓展模块的根目录
  })
  .extend({
    //设定模块别名
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
