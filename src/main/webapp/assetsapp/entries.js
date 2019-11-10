var path = require('path');
const fg = require('fast-glob');

// 允许的后缀名
const exts = ['js', 'jsx', 'ts', 'tsx'];

const base = './src/bundle/';

// 拼接成完整的路径，通配符
const inputs = exts.map(i => `${base}**/*.${i}`);

// 取得所有文件路径
const files = fg.sync(inputs);

// webpack 的 entry 是 map {key: value} 的结构
// 这里可以有相对路径，这样在 webpack.output 里就会生成相应的目录
const entries = files.reduce((acc, curr) => {
  const subdir = curr.replace(base, '');
  const idx = subdir.lastIndexOf('.');
  const name = subdir.substring(0, idx);

  const e = { [name]: curr };
  const n = { ...acc, ...e };
  return n;
}, {});

// console.log('parsing entries: ', entries);

// 输出的就是 entries
module.exports = entries;
