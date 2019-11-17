define(function(require, exports, module) {
  // 这是一个'对象构造'函数，传入的参数：用以设置内部状态；返回值：一个对象；
  const Router = ({ routes, base, targetID }) => {
    const domID = targetID || 'router-outlet';

    // html 中的目标
    const _outlet = document.getElementById(domID);

    // 取得当前 url
    const _currUrl = () => location.hash.slice(1);

    // 把 route.path 根据 / 拆分成单个的数组
    const _splitPath = routes => {
      return routes.map(r => ({
        ...r,
        segments: r.path.split('/').slice(1),
        params: {}
      }));
    };

    const _routes = _splitPath(routes);

    // 渲染 route
    const _doRender = route => route.render(route.params);

    // 程序中都是用 go 来改变 url，不触发 hashchange，但是需要保存到 history 中；
    // 通过 前进/后退 会触发 hashchange，不能保存到 history；
    const go = (targetUrl, fromHash) => {
      if (targetUrl.length === 0 || targetUrl[0] !== '/') {
        console.error('go must start with /, but got: ', targetPath);
        return;
      }

      const hashchange = fromHash || false;

      if (!hashchange) {
        // 程序中指定的 url 和 当前 url 一样，不能重复保存到 history
        if (targetUrl === _currUrl()) {
          // console.log('the same url.');
          return;
        }

        // 补上 base, 保存到 history
        const url = `${base}/#${targetUrl}`;
        history.pushState({}, '', url);
      }

      // 根据 url 找到对应的 route object, 并解析 path parameters
      const matched = _matchUrlToRoute(targetUrl);

      _outlet.innerHTML = _doRender(matched);
    };

    const _matchUrlToRoute = targetUrl => {
      // 总体逻辑：不同字符串比较，而是把字符串按照 / 拆分成 array，比较里面的每一项；如果是 path variable, 则跳过；
      // targetUrl 必须以 / 开头，所以去掉第一个对象；
      const urlSegs = targetUrl.split('/').slice(1);
      const params = {};

      // Try and match the URL to a route.
      const matchedRoute = _routes.find(r => {
        const pathSegs = r.segments;

        // If there are different numbers of segments, then the route does not match the URL.
        if (pathSegs.length !== urlSegs.length) {
          return false;
        }

        // If each segment in the url matches the corresponding segment in the route path,
        // or the route path segment starts with a ':' then the route is matched.
        const match = pathSegs.every((seg, i) => {
          return seg === urlSegs[i] || seg[0] === ':';
        });

        // If the route matches the URL, pull out any params from the URL.
        if (match) {
          pathSegs.forEach((seg, i) => {
            if (seg[0] === ':') {
              const propName = seg.slice(1);
              params[propName] = decodeURIComponent(urlSegs[i]);
            }
          });
        }

        return match;
      });

      return { ...matchedRoute, params };
    };

    // 初始页，没必要
    const bootstrap = () => {
      // Figure out the path segments for the route which should load initially.
      const pathnameSplit = window.location.pathname.split('/');
      const pathSegments =
        pathnameSplit.length > 1 ? pathnameSplit.slice(1) : '';

      console.log('bootstrap: ', pathSegments);

      // Load the initial route.
      goRoute(...pathSegments);
    };

    const _hashchange = params => {
      const r = _currUrl();

      go(r, true);
    };

    window.addEventListener('hashchange', _hashchange);

    // 返回的结果
    const result = {
      bootstrap,
      go
    };

    return result;
  };

  const demo = () => {
    // [{path, render, segemnts, params}]
    // 这里只输入 path, render;
    // segments 会自动拆分，params 则是路径匹配时，从 targeturl 中解析出来的
    const routes = [
      {
        path: '/',
        render: params => '<h1>Home</h1>'
      },
      {
        path: '/about',
        render: params => '<h1>About</h1>'
      },
      {
        path: '/contact',
        render: params => '<h1>Contact</h1>'
      },
      {
        path: '/products/:productId',
        render: params => `<h1>Product ${params.productId}</h1>`
      },
      {
        path: '/somewhere/:msg',
        render: params => `<h1>go to ${msg}</h1>`
      }
    ];

    const baseURL = '/pages/helloRouter';

    const router = Router({ routes, base: baseURL, targetID: 'router-outlet' });

    router.go('/somewhere/hahaha');
  };

  //   use 时，拿到的都是 { Router }
  exports.Router = Router;
});
