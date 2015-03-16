var gulp = require('gulp');
var sass = require('gulp-sass');
var connect = require('gulp-connect');
var concatCss = require('gulp-concat-css');
var config = require('../config.js').sass;
var configVendor = require('../config.js').vendor;

gulp.task('styles', function() {
  gulp.src(config.src)
    .pipe(sass(config.settings))
    .pipe(concatCss("bundle.css"))
    .pipe(gulp.dest(config.dest));

  gulp.src(configVendor.css.src)
    .pipe(concatCss("vendor.css"))
    .pipe(gulp.dest(configVendor.css.dest))
    .pipe(connect.reload());
});
