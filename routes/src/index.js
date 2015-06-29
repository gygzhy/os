import express from 'express';
import java from 'java';
import path from 'path';

const router = express.Router();

// pushing java path
const javaPath = path.join(__dirname, '..', 'java_util');
java.classpath.push(path.join(javaPath, 'Test1.jar'));

var Test = java.import('com.csu.Test1');
var test = new Test();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'test' });
});

module.exports = router;
