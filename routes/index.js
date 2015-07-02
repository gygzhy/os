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
_java2['default'].classpath.push(_path2['default'].join(javaPath, 'os.jar'));

var log = console.log;
var Memory = _java2['default']['import']('com.csu.os.resource.Memory');

// counting the time
var seconds = 0;

/* GET home page. */
router.get('/', function (req, res, next) {

  initIo(req.app.io);
  res.render('index', { title: 'Memory test' });
});

var ioInitialized = false;
function initIo(io) {
  if (ioInitialized) return;

  io.on('connection', function (socket) {
    log('a client connet through socket');

    socket.on('allocate memory', function (data) {
      if (isFinit(data)) var mem = Memory.Allocate(data);
      socket.emit('allocate memory', mem.getIdSync());
    });

    socket.on('change allocate mode', function (data) {});
  });

  var timer = setInterval(function () {
    Memory.Allocate(231);

    var data = {};
    data.seconds = seconds;
    data.memory = {
      sectionNum: Memory.getMemoryAllSectionNumSync(),
      idleSectionNum: Memory.getMemoryIdleSectionNumSync(),
      busySectionNum: Memory.getMemoryBusySectionNumSync(),
      idleSize: Memory.getIdleSizeSync(),
      busySize: Memory.getTotalSizeSync() - Memory.getIdleSizeSync()
    };

    data.memory.sections = [];
    var cur = Memory.getHeadSync();
    console.log(Memory.getMemoryAllSectionNumSync());
    for (var i = 0; i < Memory.getMemoryAllSectionNumSync(); i++) {
      data.memory.sections.push({
        id: cur.getIdSync(),
        size: cur.getSizeSync()
      });
      cur = cur.getNextSync();
    }

    // increase counting
    seconds++;

    io.emit('beat', data);
    log('beat');
  }, 1000);

  ioInitialized = true;
}

module.exports = router;