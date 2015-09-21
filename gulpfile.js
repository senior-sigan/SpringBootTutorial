'use strict';

var gulp = require('gulp');
var yaml = require('js-yaml');
var fs = require('fs');
var concat = require('gulp-concat');

function readAssetsVersion() {
  var version = '';

  try {
    var doc = yaml.safeLoad(fs.readFileSync('target/classes/config/application-prod.yml'));
    version = doc.assets.version;
  } catch (e) {
    console.error('Fatal error. Before running scripts packaging package spring application');
    version = '';
  }

  return version;
}

gulp.task('scripts', function() {
  var version = readAssetsVersion();
  return gulp
    .src('./src/main/resources/public/resources/scripts/*.js')
    .pipe(concat('bundle'+version+'.js'))
    .pipe(gulp.dest('./src/main/resources/public/dist/scripts/'));
});

gulp.task('stylesheets', function() {
  var version = readAssetsVersion();
  return gulp
    .src('./src/main/resources/public/resources/stylesheets/*.css')
    .pipe(concat('bundle'+version+'.css'))
    .pipe(gulp.dest('./src/main/resources/public/dist/stylesheets/'));
});

gulp.task('default', ['scripts', 'stylesheets']);
