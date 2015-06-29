import gulp from 'gulp';
import gulpLoadPlugins from 'gulp-load-plugins';
import browserSync from 'browser-sync';
import del from 'del';

const $ = gulpLoadPlugins();
const reload = browserSync.reload;

// Copy web fonts to dist
gulp.task('fonts', () => {
  return gulp.src(['app/fonts/**'])
    .pipe(gulp.dest('dist/fonts'))
    .pipe($.size({title: 'fonts'}));
});

gulp.task('nodemon', () => {
	return $.nodemon({
	  script: 'bin/www',
    env: {'NODE_ENV': 'development'},
    tasks: ['build']
	});
});

gulp.task('browswerSync', ['nodemon'], () => {
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

gulp.task('build', () => {
  return gulp.src('routes/src/*.js')
    .pipe($.babel())
    .pipe(gulp.dest('routes/'));
});

gulp.task('default', ['nodemon']);
