const gulp = require('gulp');
const sourcemaps = require('gulp-sourcemaps');
const babel = require('gulp-babel');
const uglify = require('gulp-uglify-es').default;
const del = require('del');

var concat = require('gulp-concat');

var concatCss = require('gulp-concat-css');

const jsSrc = './src/plainjs/**/*.js';
const dist = '../assets/js';

// const dist = './dist';

// 2019/11/10
// 1. 只处理 js es2015 -> es5 ，并且是单文件，不打包；
// 2. 使用 layui 的 模块机制；在 laymount.js 中配置模块的目录；
// 3. 如果要打包，使用 webpack；

gulp.task('compile', function(done) {
  return gulp
    .src(jsSrc)
    .pipe(sourcemaps.init())
    .pipe(
      babel({
        presets: ['@babel/preset-env']
      })
    )
    .pipe(sourcemaps.write('.'))
    .pipe(gulp.dest(dist))
    .on('end', done);
});

gulp.task('build', function(done) {
  return gulp
    .src(jsSrc)
    .pipe(
      babel({
        presets: ['@babel/preset-env']
      })
    )
    .pipe(uglify())
    .pipe(gulp.dest(dist))
    .on('end', done);
});

gulp.task('clean', function() {
  return del(['../assets/js/**/*', '!../assets/js'], { force: true });
});

gulp.task('dev', function() {
  return gulp.watch(jsSrc, { delay: 500 }, gulp.series('compile'));
});

// 把所有不变动的 js 变成一个的 js，一次请求所有
gulp.task('js:vendor', function(callback) {
  return (
    gulp
      .src([
        '../assets/lib/jquery-3.4.1.min.js',
        '../assets/lib/lodash-4.17.4.min.js',
        '../assets/lib/numeral.min.js',
        '../assets/lib/jquery.form.min.js',

        '../assets/lib/echarts/echarts.min.js',
        '../assets/lib/echarts/echarts.theme.js',
        '../assets/lib/echarts/dataTool.min.js',

        // 这两个文件很大，需要时再单独引用
        // '../assets/lib/g2/g2.min.js',
        // '../assets/lib/g2/data-set.min.js',

        '../assets/lib/axios.min.js',

        '../assets/lib/fontawesome/all.js'
      ])
      .pipe(sourcemaps.init())
      // getBundleName creates a cache busting name
      .pipe(concat('vendor.js'))
      //   所有 vendor 直接使用 min 后的版本
      //   .pipe(uglify())
      .pipe(sourcemaps.write('./'))
      .pipe(gulp.dest(dist))
  );
});

gulp.task('css:vendor', function() {
  return gulp
    .src([
      '../assets/css/okadmin.css',
      '../assets/lib/fontawesome/font-awesome.min.css',
      '../assets/lib/bulma/css/bulma.min.css'
    ])
    .pipe(concatCss('vendor.css'))
    .pipe(gulp.dest('../assets/js'));
});

// 最终的编译
gulp.task('prod', gulp.series('clean', 'build', 'js:vendor', 'css:vendor'));
