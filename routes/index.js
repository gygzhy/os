'use strict';

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var _express = require('express');

var _express2 = _interopRequireDefault(_express);

var _java = require('java');

var _java2 = _interopRequireDefault(_java);

var _path = require('path');

var _path2 = _interopRequireDefault(_path);

var router = _express2['default'].Router();

// pushing java path
var javaPath = _path2['default'].join(__dirname, '..', 'java_util');
_java2['default'].classpath.push(_path2['default'].join(javaPath, 'Test1.jar'));

var Test = _java2['default']['import']('com.csu.Test1');
var test = new Test();

/* GET home page. */
router.get('/', function (req, res, next) {
  res.render('index', { title: 'go fcsaefdf' });
});

module.exports = router;