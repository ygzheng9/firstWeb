const path = require('path');
const webpack = require('webpack');

const CompressionPlugin = require('compression-webpack-plugin');

const output = __dirname + '/../assets/js/bundle';

module.exports = {
  entry: {
    // 第三方库
    vendor: ['react', 'react-dom', '@antv/g2', '@antv/data-set', 'zeu']
  },
  output: {
    path: output,
    // 输出的动态链接库的文件名称，[name] 代表当前动态链接库的名称，
    filename: '[name].dll.js',
    // library必须和后面dllplugin中的name一致 后面会说明
    library: '[name]_dll_[hash]'
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
    // 接入 DllPlugin
    new webpack.DllPlugin({
      // 描述动态链接库的 manifest.json 文件输出时的文件名称
      path: path.join(output, '[name].manifest.json'),
      // 动态链接库的全局变量名称，需要和 output.library 中保持一致
      // 该字段的值也就是输出的 manifest.json 文件 中 name 字段的值
      name: '[name]_dll_[hash]'
    })
  ]
};
