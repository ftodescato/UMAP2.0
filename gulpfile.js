var gulp = require('gulp');
var concat = require('gulp-concat');
var jade = require('gulp-jade');
var watch = require('gulp-watch');

gulp.task('js', function () {
  gulp.src([
  'vendor/bower_components/jquery/dist/jquery.min.js',
  'vendor/bower_components/angular/angular.js',
  'vendor/bower_components/angular-ui-router/release/angular-ui-router.js',
  //'public/frontend/vendor/bower_components/angular-resource/angular-resource.js',
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
