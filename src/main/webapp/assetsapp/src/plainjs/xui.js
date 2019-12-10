// 只需要执行一次，所以不需要放到 turbolinks:load 中；

(function iife(window) {
  function inner() {
    // pure id
    // #leftMenu -> leftMenu;
    function getPureID(domId) {
      if (domId === undefined || domId.length === 0) {
        return '';
      }

      if (domId.charAt[0] === '#') {
        return domId.substring(1, domId.length - 1);
      }

      return domId;
    }

    // 返回带 # 的 id；
    function getJqueryID(domId) {
      if (domId === undefined || domId.length === 0) {
        return '';
      }

      if (domId.charAt[0] === '#') {
        return domId;
      }

      return '#' + domId;
    }

    // 顶部导航，屏幕变窄后，item 隐藏，在右边出现面包条
    // initBulmaBurger('');
    function initBulmaBurger() {
      var burger = document.querySelector('.burger');
      var menu = document.querySelector('#' + burger.dataset.target);
      burger.addEventListener('click', function() {
        burger.classList.toggle('is-active');
        menu.classList.toggle('is-active');
      });
    }

    // 左侧菜单, 点击第一级，显示/隐藏 第二级
    // 使用： initMenu('#leftMenu');
    function initMenu(domId) {
      // 显示/隐藏 下一级菜单
      const topLevels = document.querySelectorAll(`${domId} p.menu-label`);
      topLevels.forEach(el => {
        el.addEventListener('click', e => {
          el.nextElementSibling.classList.toggle('is-hidden');
        });
      });

      // 菜单项的选中，菜单项都是 a
      const items = document.querySelectorAll(`${domId} ul.menu-list li a`);
      items.forEach(el => {
        el.addEventListener('click', () => {
          // 清除之前选中
          items.forEach(node => {
            node.classList.remove('is-active');
          });

          // 选中当前
          el.classList.add('is-active');
        });
      });
    }

    // 选中当前页面的 menu item
    function selectMenu(...args) {
      if (args.length === 0) {
        return;
      }

      let currURL = '/';
      if (args.length >= 1) {
        currURL = args[0];
      }

      const items = document.querySelectorAll(`#leftMenu ul.menu-list li a`);
      items.forEach(el => {
        if (el.getAttribute('href') === currURL) {
          // 标记当前页面的 item 为选中；
          el.classList.add('is-active');
        } else {
          // 清除选中标记
          el.classList.remove('is-active');
        }
      });

      // 如果传入了 title，则设置
      if (args.length >= 2) {
        document.title = args[1];
      }
    }

    // 显示/隐藏 左侧菜单，左右都显示时，需要指定 列宽；
    // toggleLeftMenu('toggleLeft', 'leftMenuContainer', 'mainContainer');
    function toggleLeftMenu(targetID, menuID, mainID) {
      const el = document.getElementById(targetID);
      const menu = document.getElementById(menuID);
      const content = document.getElementById(mainID);
      el.addEventListener('click', () => {
        menu.classList.toggle('is-hidden');
        content.classList.toggle('is-9');
      });
    }

    // 初始化 remoteLink
    // initRemoteLink('#leftMenu a[data-remote]', {targetID: 'mainContainer'});
    function initRemoteLink(selector, options) {
      // 优先从 options 中取出 target
      let target = '';
      if (options !== undefined && options.targetID !== undefined) {
        target = options.targetID;
      }

      const links = document.querySelectorAll(selector);
      links.forEach(el => {
        // 如果没有 data-remote 属性，直接返回
        if (el.dataset.remote === undefined) {
          return;
        }

        const url = el.dataset.url;
        let selfTarget = target;
        if (selfTarget.length === 0) {
          selfTarget = el.dataset.target;
        }

        if (selfTarget.length === 0) {
          return;
        }

        if (selfTarget.charAt(0) !== '#') {
          selfTarget = `#${selfTarget}`;
        }

        console.log('doRemote: ', url, ' --> ', selfTarget);

        // 设置 handleClick 事件
        el.addEventListener('click', () => {
          $.get(url).then(html => {
            $(selfTarget).html(html);
          });
        });
      });
    }

    // document.ready
    function onReady(callback) {
      if (
        document.attachEvent
          ? document.readyState === 'complete'
          : document.readyState !== 'loading'
      ) {
        callback();
        // console.log('document ready');
      } else {
        document.addEventListener('DOMContentLoaded', callback);
      }
    }

    // helper: onClick
    function onClick(domId, handler) {
      document.getElementById(domId).addEventListener('click', handler);
    }

    // 检查是否有重复 id
    function checkSameIDs() {
      const divs = document.get('[id]');

      const all = [];
      divs.forEach(d => {
        const i = all.findIndex(a => a === d.id);
        if (i === -1) {
          all.push(d.id);
          return;
        }
        console.log('dup: ', d.id);
      });
    }

    // 优先考虑 Turbolinks，所以暂且不使用 pjax
    function initPjax() {
      $(document).pjax('[data-pjax] a, a[data-pjax]', '#mainContainer');
      $(document).on('pjax:start', () => {
        NProgress.start();
      });
      $(document).on('pjax:end', () => {
        NProgress.done();
      });
    }

    // 简易的事件处理系统
    function EventBus() {
      // [{eventType, callbacks: [cb1, cb2]}, {}, {}]
      const eventTables = [];

      function createEventEntry(eventType, callback) {
        return { eventType, callbacks: [callback] };
      }

      function findEventEntry(eventType) {
        return eventTables.find(entry => entry.eventType === eventType);
      }

      function subscribe(eventType, callback) {
        const entry = findEventEntry(eventType);

        if (entry !== undefined) {
          entry.callbacks.push(callback);
        } else {
          eventTables.push(createEventEntry(eventType, callback));
        }
      }

      function post(eventType, args) {
        const entry = findEventEntry(eventType);

        if (!entry) {
          console.error(`no subscribers for event ${eventType}`);
          return;
        }

        entry.callbacks.forEach(callback => callback(args));
      }

      return {
        subscribe,
        post,
        findEventEntry,
        createEventEntry
      };
    }

    // Turbolinks 配置
    function initTurboLinks() {
      Turbolinks.start();
      Turbolinks.setProgressBarDelay(100);
    }

    function visit(url) {
      TurboLinks.visit(url);
    }

    // turbolinks:load 所有的事件
    // {domId, fn}
    const tbLoadTable = [];
    function tbAddLoad(domId, fn) {
      if (domId === undefined || fn === undefined) {
        console.log('tbAddLoad wrong params, must be (domId, fn)');
        return;
      }

      for (let i = 0; i < tbLoadTable.length; i++) {
        const a = tbLoadTable[i];
        if (a.domId === domId) {
          console.log('WARNING: dulpication domId, override. ', domId);
          break;
        }
      }

      // 手工执行一次
      fn();

      // 加入到 list 中，之后 turbolinks 每次 load 都会执行；
      tbLoadTable.push({ domId, fn });
    }

    function tbFireLoad() {
      const items = tbLoadTable;
      for (let i = 0; i < items.length; i++) {
        const { domId, fn } = items[i];

        const d = document.getElementById(domId);

        console.log('trying: ' + domId);

        // 没有目标dom
        if (d == null || d === undefined) continue;

        // 有目标dom，但是已经被初始化过了；
        if (d.hasAttribute('data-inited')) continue;

        // 执行初始化，并且设置 flag，放置重复执行
        console.log('trying: ' + domId + ' ---> matched.');
        d.setAttribute('data-inited', 'true');
        fn();
      }
    }

    // 对 Turbolinks:load 事件函数的包装，通过设置 data-inited，保证 Idempotent 等幂性(执行 n 次和执行一次的结果相同)
    function tbIdemWrapper(domId, fn) {
      return function() {
        const d = document.getElementById(domId);

        console.log('trying: ' + domId);

        // 没有目标dom
        if (d == null || d === undefined) return;

        // 有目标dom，但是已经被初始化过了；
        if (d.hasAttribute('data-inited')) return;

        console.log('trying: ' + domId + ' ---> matched.');

        fn();

        d.setAttribute('data-inited', 'true');
      };
    }

    // 非首页的 js 中使用，也即：如果使用了 tbLoad 的 html，不能作为首页，除非加上 defer
    // domId 是一个标记div，用来控制 fn 是否执行，满足 Turbolinks 文档中所要求的 Idemponent 性质
    function tbLoad(domId, fn) {
      // 非首页第一次打开时，新 js 中的 load 并不执行，所以手工执行一次，
      fn();

      // 每次页面跳转，load 都执行
      document.addEventListener('turbolinks:load', tbIdemWrapper(domId, fn));
    }

    // layui 的 配置
    function initLayui() {
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
    }

    function initSea() {
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
    }

    // 根据 后台传入的 actionKey，设置页面中菜单的选中状态
    function highligtMenu() {
      // actionKeyID 是后台传入的，记录了 actionKey
      const selectedMenu = $('#actionKeyID').attr('data-menu');

      //   顶部菜单
      const navSelector = `.navbar-dropdown a[href='${selectedMenu}']`;
      const navs = $(navSelector);
      if (navs.length > 0) {
        navs.addClass('is-active');
      }

      //    左侧菜单
      const menuSelector = `.menu-list a[href='${selectedMenu}']`;
      const menus = $(menuSelector);
      if (menus.length > 0) {
        menus.addClass('is-active');
      }
    }

    function getUniq(list, name) {
      let a = list.map(i => i[name]);
      a = _.uniq(a);
      return a;
    }

    function setHtml(url, domid) {
      $.get(url, res => {
        // 直接赋值
        document.getElementById(domid).innerHTML = res;

        // jquery 是函数
        // $(domid).text(res);
      });
    }

    /**
     * 根据form表单的id获取表单下所有可提交的表单数据，封装成数组对象
     */
    function getFormData(formId) {
      var data = {};
      var results = $(formId).serializeArray();
      $.each(results, function(index, item) {
        //文本表单的值不为空才处理
        if (item.value && $.trim(item.value) !== '') {
          if (!data[item.name]) {
            data[item.name] = item.value;
          } else {
            //name属性相同的表单，以 空格 拼接
            data[item.name] = data[item.name] + ' ' + item.value;
          }
        }
      });
      //console.log(data);
      return data;
    }

    // select 后面紧跟一个 hidden，在 hidden 中有 select 的值
    function initSelectOptions(formID) {
      $(`${formID} select`).each(function() {
        const self = $(this);
        const v = self.next().val();
        if (v === undefined || v.length === 0) {
          return;
        }
        self.val(v);
      });
    }

    return {
      getPureID,

      initBulmaBurger,

      initMenu,
      selectMenu,
      toggleLeftMenu,
      initRemoteLink,
      onReady,
      onClick,

      checkSameIDs,

      initPjax,

      EventBus: EventBus(),

      initLayui,
      initTurboLinks,
      initSea,
      highligtMenu,

      getUniq,
      setHtml,
      getFormData,
      initSelectOptions,

      tbIdemWrapper,
      tbLoad,

      tbFireLoad,
      tbAddLoad,
      visit
    };
  }

  // global, make sure not use the same name other where!!
  // xui 是一个对象，里面有各种属性，对应不同的函数；
  window.xui = inner();

  // 立即执行
  xui.initTurboLinks();
  xui.initLayui();

  // 只有这一个地方注册事件，其它页面如果要注册，那么使用 tbAddLoad，增加到 table 中
  document.addEventListener('turbolinks:load', xui.tbFireLoad);

  xui.tbAddLoad('landingPageID', function() {
    xui.initBulmaBurger();
    xui.highligtMenu();
  });

  // 感觉这里是个 bug，因为在 tbAddLoad 中已经执行过一次了，但是没有效果，所有这里有执行一次；
  xui.initBulmaBurger();
})(window);
