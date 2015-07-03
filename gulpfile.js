var gulp = require('gulp');
var gulpLoadPlugins = require('gulp-load-plugins');
var browserSync = require('browser-sync');
var del = require('del');

var $ = gulpLoadPlugins();
var reload = browserSync.reload;

// Copy web fonts to dist
gulp.task('fonts', function() {
  return gulp.src(['app/fonts/**'])
    .pipe(gulp.dest('dist/fonts'))
    .pipe($.size({title: 'fonts'}));
});

gulp.task('nodemon', function() {
	return $.nodemon({
	  script: 'bin/www',
    ext: 'js',
    watch: ['routes']
	}).on('restart', function() {
    console.log('restarted');
  });
});

gulp.task('test', function() {
    return gulp.src('./test/memory.js')
        .pipe($.jasmine());
});

gulp.task('browswerSync', ['nodemon'], function() {
  return browserSync({
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

gulp.task('build', function() {
  gulp.src(['routes/src/*.js'])
    .pipe($.babel())
    .pipe(gulp.dest('routes/'));

  return gulp.src(['./src/app.js'])
      .pipe($.babel())
      .pipe(gulp.dest('./'));
});

gulp.task('default', ['browswerSync', 'nodemon']);
