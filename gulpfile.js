'use strict';

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var _gulp = require('gulp');

var _gulp2 = _interopRequireDefault(_gulp);

var _gulpLoadPlugins = require('gulp-load-plugins');

var _gulpLoadPlugins2 = _interopRequireDefault(_gulpLoadPlugins);

var _browserSync = require('browser-sync');

var _browserSync2 = _interopRequireDefault(_browserSync);

var _del = require('del');

var _del2 = _interopRequireDefault(_del);

var $ = (0, _gulpLoadPlugins2['default'])();
var reload = _browserSync2['default'].reload;

// Copy web fonts to dist
_gulp2['default'].task('fonts', function () {
  return _gulp2['default'].src(['app/fonts/**']).pipe(_gulp2['default'].dest('dist/fonts')).pipe($.size({ title: 'fonts' }));
});

_gulp2['default'].task('nodemon', function () {
  return $.nodemon({
    script: 'bin/www',
    ext: 'js',
    watch: ['routes'],
    tasks: ['build']
  }).on('restart', function () {
    console.log('restarted');
  });
});

_gulp2['default'].task('test', function () {
  return _gulp2['default'].src('./test/memory.js').pipe($.jasmine());
});

_gulp2['default'].task('browswerSync', ['nodemon'], function () {
  return (0, _browserSync2['default'])({
    notify: false,
    // Customize the BrowserSync console logging prefix
    logPrefix: 'WSK',
    // Run as an https by uncommenting 'https: true'
    // Note: this uses an unsigned certificate which on first access
    //       will present a certificate warning in the browser.
    // https: true,
    proxy: 'http://localhost:3000',
    port: 7000,
    files: ['public/**/*.*', 'views/**/*.*', 'routes/**/*.*']
  });
});

_gulp2['default'].task('build', function () {
  _gulp2['default'].src(['routes/src/*.js']).pipe($.babel()).pipe(_gulp2['default'].dest('routes/'));

  return _gulp2['default'].src(['./src/app.js']).pipe($.babel()).pipe(_gulp2['default'].dest('./'));
});

_gulp2['default'].task('default', ['browswerSync', 'nodemon']);
