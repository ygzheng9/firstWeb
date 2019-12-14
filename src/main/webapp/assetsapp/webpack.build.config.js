const path = require('path');
const webpack = require('webpack');

const CompressionPlugin = require('compression-webpack-plugin');

// 2019/11/10
// 对 js 的打包 webpack 其实很简单
// 只需要编译 js，然后手工在后台渲染的 html 中引用，所以不需要自动生成 html
// const HtmlWebPackPlugin = require("html-webpack-plugin");

// 没有使用 dev-server，而是使用了 --watch，目的是在编译后的 js，供后台渲染的 html 引用
// dev-server 是内存模式，有独立的 web server

// 在 package.json 的 scripts 中定义
// "dev": "webpack --mode development --watch --devtool inline-source-map",
// "build": "webpack --mode production"

// 所有的入口文件都放在这
// 入口文件可以有多级目录，但是在 entry 的 key 中要体现相对路径，这样在 output 时，会创建相应的目录
const entry = require('./entries');
const entries = entry.getEntries('./src/bundle/');

const output = __dirname + '/../assets/js/bundle';

console.log('=====================');
console.log('entries: ', entries);
console.log('output: ', output);
console.log('=====================');

module.exports = {
  mode: 'production',
  devtool: false,
  //   entry: {
  //     'js/hello': __dirname + '/src/bundle/hello.tsx'
  //   },
  entry: entries,
  output: {
    filename: '[name].js',
    path: output
  },
  module: {
    rules: [
      {
        test: /\.jsx?$/,
        exclude: /node_modules/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-env', '@babel/preset-react'],
            plugins: [
              '@babel/plugin-proposal-object-rest-spread',
              '@babel/plugin-proposal-class-properties'
            ]
          }
        }
      },
      // tsconfig.json is used for ts-loader
      { test: /\.tsx?$/, use: 'ts-loader' },
      { test: /\.css$/, use: 'css-loader' }
    ]
  },
  resolve: {
    // import 忽略后缀名时，后缀名的查找策略
    extensions: ['.js', '.jsx', '.ts', '.tsx', '.json'],
    // import 相对路径的查找,
    // 需要和 tscofnig/json 中的 paths 相对应，这样 vscode 就能找到文件；
    alias: {
      Components: path.resolve(__dirname, 'src/app/components')
    }
  },
  externals: {
    // 使用 import $ from 'jquery'，但是 jquery 不会被打包，需要直接在 html 中 <script src="jquery.js" />
    jquery: 'jQuery',
    lodash: '_',
    axios: 'axios',
    echarts: 'echarts',
    numeral: 'numeral',
    react: 'React',
    'react-dom': 'ReactDOM'
  },
  plugins: [
    new CompressionPlugin({
      filename: '[path].br[query]',
      algorithm: 'brotliCompress',
      test: /\.(js|css|html|svg)$/,
      compressionOptions: { level: 11 },
      threshold: 10240,
      minRatio: 0.8,
      deleteOriginalAssets: false
    }),
    new CompressionPlugin({
      filename: '[path].gz[query]',
      algorithm: 'gzip',
      test: /\.(js|css|html|svg)$/,
      threshold: 10240,
      minRatio: 0.8
    }),
    new webpack.DllReferencePlugin({
      context: __dirname,
      manifest: require(output + '/vendor.manifest.json')
    })
  ]
};
