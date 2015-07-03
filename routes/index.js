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
var cpu = new _java2['default']['import']('com.csu.os.managers.CPUManager')();
var pcbManager = cpu.getPcbManagerSync();
var Parameter = _java2['default']['import']('com.csu.os.tools.Parameter');

cpu.start();
// counting the time
var seconds = 0;

/* GET home page. */
router.get('/', function (req, res, next) {

  initIo(req.app.io);
  res.render('index', { title: 'Memory fuck hey' });
});

var ioInitialized = false;
function initIo(io) {
  if (ioInitialized) return;

  var j = 0;
  var memorys = [];
  var interval = 1000;

  io.on('connection', function (socket) {
    log('a client connet through socket');

    socket.on('allocate memory', function (data) {
      if (isFinit(data)) var mem = Memory.Allocate(data);
      socket.emit('allocate memory', mem.getIdSync());
    });

    socket.on('change allocate mode', function (data) {});

    socket.on('change cpu interval', function (data) {
      console.log('interval change to ' + data);
      //interval = data;
      Parameter.SLEEP_TIME = data;
    });

    socket.on('add a process', function (data) {
      console.log('a process is about to be created');
      try {
        pcbManager.addPCBSync(data.name, data.user, parseInt(data.memory), parseInt(data.level), parseInt(data.time));
      } catch (e) {
        console.log('adding pcb fail' + e);
      }
    });

    socket.on('change mode memory', function (data) {
      console.log('change mode memory');

      Memory.setAllocateModeSync(Memory.mode[data]);
    });

    socket.on('change mode cpu', function (data) {
      console.log('change mode cpu');

      pcbManager.setArithmeticStatusSync(parseInt(data));
    });

    socket.on('user login', function (data) {
      console.log('user ' + data + ' login');
    });
  });

  var timer = function timer() {
    var data = {};
    data.seconds = seconds;

    // get the pcb infomation from java
    getPcbInfo(data);
    // get the memory informatino from java
    getMemoryInfo(data);
    // increase counting
    seconds += interval / 1000;

    io.emit('heart beat', data);
    log('heart beat');

    setTimeout(timer, interval);
  };

  timer();

  ioInitialized = true;
}

function getPcbInfo(data) {
  data.pcbs = {
    init: pcbArray(pcbManager.getInitPCBListSync()),
    ready: pcbArray(pcbManager.getReadyPCBListSync()),
    wait: pcbArray(pcbManager.getWaitPCBListSync()),
    finish: pcbArray(pcbManager.getFinishPCBListSync()),
    total: pcbArray(pcbManager.getTotalPCBListSync())
  };

  function pcbArray(pcbList) {
    var ret = [];
    for (var i = 0; i < pcbList.sizeSync(); i++) {
      var pcb = pcbList.getSync(i);
      var pid = pcb.getpIdStringSync();
      var uid = pcb.getuIdStringSync();
      ret.push({
        id: pid,
        uid: uid,
        name: pcb.getNameSync(),
        cpuTime: pcb.getRunTimeSync(),
        memorySize: pcb.getMemorySync().getSizeSync(),
        level: pcb.getLevelSync(),
        status: pcb.getStatusSync(),
        user: pcb.getUserSync()
      });
    }
    return ret;
  }
}

function getMemoryInfo(data) {
  data.memory = {
    sectionNum: Memory.getMemoryAllSectionNumSync(),
    idleSectionNum: Memory.getMemoryIdleSectionNumSync(),
    busySectionNum: Memory.getMemoryBusySectionNumSync(),
    idleSize: Memory.getIdleSizeSync(),
    busySize: Memory.getTotalSizeSync() - Memory.getIdleSizeSync()
  };

  data.memory.sections = [];
  var cur = Memory.getHeadSync();
  for (var i = 0; i < Memory.getMemoryAllSectionNumSync(); i++) {
    var pcb = cur.getPcbSync();
    data.memory.sections.push({
      id: cur.getIdSync(),
      size: cur.getSizeSync(),
      isIdle: cur.isIdleSync(),
      pcbName: pcb ? pcb.getNameSync() : null
    });
    cur = cur.getNextSync();
  }
}

process.on('exit', function (code) {
  cpu.stop();
  console.log('About to exit with code:', code);
});

process.on('SIGINT', function () {
  console.log('interrupt signal');
  process.exit();
});

module.exports = router;