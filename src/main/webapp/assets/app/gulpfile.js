const gulp = require('gulp');
const sourcemaps = require('gulp-sourcemaps');
const babel = require('gulp-babel');
const rename = require('gulp-rename');
const uglify = require('gulp-uglify-es').default;
const del = require('del');
const mergeStream = require('merge-stream');

var ts = require('gulp-typescript');
var tsProject = ts.createProject('tsconfig.json', { declaration: true });

const jsSrc = './src/**/*.js';
const dist = '../js';
// const dist = './dist';

// 只做最简单的 2015/ts -> es5, 不打包
var stream1 = gulp.src(jsSrc);
// var stream2 = tsProject.src().pipe(tsProject()).js;
// var merged = mergeStream(stream1, stream2);
var merged = stream1;

function js() {
  return merged
    .pipe(sourcemaps.init())
    .pipe(
      babel({
        presets: ['@babel/preset-env']
      })
    )
    .pipe(sourcemaps.write('.'))
    .pipe(gulp.dest(dist));
}

function build() {
  return merged
    .pipe(
      babel({
        presets: ['@babel/preset-env']
      })
    )
    .pipe(uglify())
    .pipe(gulp.dest(dist));
}

gulp.task('clean', function() {
  return del(['../js/**/*', '!../js'], { force: true });
});

gulp.task('watch', function() {
  return gulp.watch(jsSrc, { delay: 500 }, gulp.series('clean', js));
});

exports.compile = gulp.series('clean', js);
exports.build = gulp.series('clean', build);

////////////////////////
// 启用打包，可以使用 require 引入文件，entries 只能是顶层文件；
// 如果打包，使用 webpack 或 rollup
var browserify = require('browserify');
var source = require('vinyl-source-stream');
const buffer = require('vinyl-buffer');
var tsify = require('tsify');

gulp.task('tsbundle', function() {
  return browserify({
    basedir: '.',
    debug: true,
    entries: ['src/test.ts'],
    cache: {},
    packageCache: {}
  })
    .plugin(tsify)
    .transform('babelify', {
      presets: ['@babel/preset-env'],
      extensions: ['.ts']
    })
    .bundle()
    .pipe(source('bundle.js'))
    .pipe(buffer())
    .pipe(sourcemaps.init({ loadMaps: true }))
    .pipe(uglify())
    .pipe(sourcemaps.write('./'))
    .pipe(gulp.dest('dist'));
});
