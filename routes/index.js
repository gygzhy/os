var express = require('express');
var router = express.Router();
var java = require('java');

java.classpath.push("../node_modules/java/test/commons-lang3-3.1.jar");

var array = java.newArray('java.lang.String', ["fuck", "this", "shit"]);


/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: array[0] });
});

module.exports = router;
