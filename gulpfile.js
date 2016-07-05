var gulp = require('gulp');
var concat = require('gulp-concat');
var jade = require('gulp-jade');
var watch = require('gulp-watch');

gulp.task('js', function () {
  gulp.src([
  'vendor/bower_components/angular/angular.js',
  'vendor/bower_components/angular-ui-router/release/angular-ui-router.js',
  'vendor/bower_components/angular-resource/angular-resource.js',
  'vendor/bower_components/angular-cookies/angular-cookies.js',
  'vendor/bower_components/angular-dragdrop/src/angular-dragdrop.min.js',
  'vendor/bower_components/jquery-ui/jquery-ui.min.js',
  'vendor/bower_components/angular-flash-alert/src/angular-flash.js',
  'vendor/bower_components/jqueryui-touch-punch/jquery.ui.touch-punch.min.js',
  'vendor/bower_components/Chart.js/Chart.js',
  'vendor/bower_components/angular-chart.js/dist/angular-chart.js',
  'app/assets/frontend/javascript/**/*.js'])
    .pipe(concat('public/js/main.js'))
    .pipe(gulp.dest('.'))
});

gulp.task('html',function() {
  gulp.src('app/assets/frontend/html/**/*.jade')
      .pipe(jade({
        pretty: true
      }))
      .pipe(gulp.dest('public/html'))
});

gulp.task('watch', function(){
  gulp.watch('app/assets/frontend/javascript/**/*.js',['js']);
  gulp.watch('app/assets/frontend/html/**/*.jade',['html']);
});

gulp.task('default',['js','html','watch']);
