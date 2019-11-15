const gulp = require('gulp');
const sourcemaps = require('gulp-sourcemaps');
const babel = require('gulp-babel');
const uglify = require('gulp-uglify-es').default;
const del = require('del');

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
