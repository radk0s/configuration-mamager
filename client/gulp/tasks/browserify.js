var gulp = require('gulp');
var gutil = require('gulp-util');
var source = require('vinyl-source-stream');
var browserify = require('browserify');
var watchify = require('watchify');
var connect = require('gulp-connect');
var config = require('../config').browserify;
var reactify = require('reactify');
var babelify = require('babelify');

watchify.args.fullPaths = false;

var bundler = browserify(config.src, watchify.args);

bundler.transform(reactify);
bundler.transform(babelify);

gulp.task('browserify', bundle);
bundler.on('update', bundle);

function bundle() {
  return bundler.bundle()
  // log errors if they happen
  .on('error', gutil.log.bind(gutil, 'Browserify Error'))
  .pipe(source(config.outputName))
  .pipe(gulp.dest(config.dest))
  .pipe(connect.reload());
}
