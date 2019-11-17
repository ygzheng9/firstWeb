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

seajs.use(['hello', 'simple', 'router'], function(hello, simple, { Router }) {
  const router = Router({ routes, base: baseURL, targetID: 'router-outlet' });

  const { sayHello, bye } = hello;

  console.log(sayHello(), bye(), simple.helloWorld());

  $('#about').on('click', () => {
    router.go('/about');
  });

  $('#products2').on('click', () => {
    router.go('/products/2');
  });
});
