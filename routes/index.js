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
var Disk = _java2['default']['import']('com.csu.os.resource.Disk');
var FCB = _java2['default']['import']('com.csu.os.resource.FCB');

var disk = new Disk(2048, 32);

cpu.start();
// counting the time
var seconds = 0;

/* GET home page. */
router.get('/', function (req, res, next) {

  initIo(req.app.io);
  res.render('index', { title: 'OS Design' });
});

// 用户列表
var users = {};

var ioInitialized = false;
function initIo(io) {
  if (ioInitialized) return;

  var j = 0;
  var memorys = [];
  var interval = 1000;

  io.on('connection', function (socket) {

    // current user;
    var user = null;

    log('a client connet through socket');

    socket.on('error', function (error) {
      console.trace(error);
    });

    socket.on('allocate memory', function (data) {
      if (isFinit(data)) {
        var mem = Memory.Allocate(data);
        socket.emit('allocate memory', mem.getIdSync());
      }
    });

    socket.on('change cpu interval', function (data) {
      console.log('interval change to ' + data);
      data = data - data % 1000;
      interval = data;
      Parameter.SLEEP_TIME = data;
    });

    socket.on('add a process', function (data) {
      console.log('a process is about to be created');
      var pcb = null;
      try {
        pcb = pcbManager.addPCBSync(data.name, data.user, parseInt(data.memory), parseInt(data.level), parseInt(data.time));
      } catch (e) {
        console.log('adding pcb fail' + e);
        return;
      }

      socket.emit('add a process', createPcb(pcb));
    });

    socket.on('change mode memory', function (data) {
      console.log('change mode memory');

      Memory.setAllocateModeSync(Memory.mode[data]);
    });

    socket.on('change mode cpu', function (data) {
      console.log('change mode cpu');

      pcbManager.setArithmeticStatusSync(parseInt(data));
    });

    // ------------------------------------------------------------------------
    // user login logout
    // ------------------------------------------------------------------------

    socket.on('add user', function (data) {
      console.log('add user');
      users[data.username] = {
        isLogin: false
      };

      var fragment = disk.getFragmentByNameSync(data.fragment);
      if (!fragment) {
        return;
      }
      var root = fragment.getRootSync();

      var folder = root.createSubFolderSync();
      folder.setNameSync(data.username);
    });

    socket.on('delete user', function (data) {
      var toDelete = users[data.username];
      toDelete.folder.deleteSync();
    });

    socket.on('user login', function (data) {
      if (users[data.username]) {
        user = users[data.username];
        user.isLogin = true;
        user.currentFolder = user.folder;

        socket.emit('user login', {
          username: data.username
        });

        socket.emit('open folder', {
          path: user.currentFolder.getPathSync(),
          subFcbs: openFolder(user.currentFolder)
        });
      }
    });

    socket.on('user logout', function (data) {
      if (user) {
        user.isLogin = false;
      }
    });

    // ------------------------------------------------------------------------
    // disk
    // ------------------------------------------------------------------------
    socket.on('new fragment', function (data) {
      var fragment = disk.addFragmentSync(parseInt(data.size), data.name);
    });

    socket.on('delete fragment', function (data) {
      var fragment = disk.getFragmentByNameSync(data.name);
      if (fragment !== null) {
        fragment['delete']();
      }
    });

    // ------------------------------------------------------------------------
    // user message
    // ------------------------------------------------------------------------
    socket.on('send message', function (data) {
      data.time = Date.now();
      data.from = user.name;
      if (data.to) {} else {
        io.emit('send message', data);
      }
    });

    // ------------------------------------------------------------------------
    // file and folder
    // ------------------------------------------------------------------------

    socket.on('new file', function (data) {
      var fcb = user.currentFolder.createSubFileSync();

      socket.emit('open folder', {
        path: user.currentFolder.getPathSync(),
        subFcbs: openFolder(user.currentFolder)
      });
    });

    socket.on('edit file', function (data) {
      var fcb = user.currentFolder.getFcbByIdSync(data.id);
      fcb.replaceSync(data.content);
      fcb.setNameSync(data.name);

      socket.emit('open folder', {
        path: user.currentFolder.getPathSync(),
        subFcbs: openFolder(user.currentFolder)
      });
    });

    socket.on('read file', function (data) {
      var fcb = user.currentFolder.getFcbByIdSync(data.id);

      console.log(fcb.getNameSync());

      socket.emit('read file', {
        id: fcb.getIdStringSync(),
        name: fcb.getNameSync(),
        content: fcb.readStringSync()
      });
    });

    socket.on('go back', function () {
      var fcb = user.currentFolder.getParentSync();

      if (fcb.getIdStringSync() === user.currentFolder.getFragmentSync().getRootSync().getIdStringSync()) {
        return;
      }
      user.currentFolder = fcb;

      socket.emit('open folder', {
        path: fcb.getPathSync(),
        subFcbs: openFolder(fcb)
      });
    });

    socket.on('open folder', function (data) {
      var fcb = user.currentFolder.getFcbByIdSync(data.id);

      user.currentFolder = fcb;

      socket.emit('open folder', {
        path: fcb.getPathSync(),
        subFcbs: openFolder(fcb)
      });
    });

    function openFolder(fcb) {
      var subFcbs = fcb.getSubFcbsSync();
      var ret = [];
      for (var i = 0; i < subFcbs.sizeSync(); i++) {
        var f = subFcbs.getSync(i);
        ret.push({
          id: f.getIdStringSync(),
          name: f.getNameSync(),
          isFolder: f.isFolderSync()
        });
      }

      return ret;
    }

    socket.on('new folder', function (data) {
      var fcb = user.currentFolder.createSubFolderSync();

      socket.emit('new folder', {
        id: fcb.getIdStringSync(),
        name: fcb.getNameSync(),
        size: fcb.getSizeSync()
      });
    });

    socket.on('delete fcb', function (data) {
      var fcb = user.currentFolder.getFcbByIdSync(data.id);
      fcb.deleteSync();

      log('delete fcb');

      socket.emit('open folder', {
        path: user.currentFolder.getPathSync(),
        subFcbs: openFolder(user.currentFolder)
      });
    });

    // ------------------------------------------------------------------------
    // pcb
    // ------------------------------------------------------------------------
    socket.on('pcb operation stop', function (data) {
      pcbManager.deletePCBSync(data.id);
    });

    socket.on('pcb operation restart', function (data) {
      pcbManager.activatePCBSync(data.id);
    });

    socket.on('pcb operation wait', function (data) {
      pcbManager.hangPCBSync(data.id);
    });

    socket.on('pcb operation message', function (data) {
      var pcb = pcbManager.getPcbByIdSync(data.id);
      pcb.productMessage(null, 0, 'Hey ' + pcb.getNameSync());
    });
  });

  var timer = function timer() {
    var data = {};
    data.seconds = seconds;

    // get the pcb infomation from java
    getPcbInfo(data);
    // get the memory informatino from java
    getMemoryInfo(data);

    getHardDiskInfo(data);

    getUserInfo(data);
    // increase counting
    seconds += interval / 1000;

    io.emit('heart beat', data);

    setTimeout(timer, interval);
  };

  timer();

  ioInitialized = true;
}

function getUserInfo(data) {
  data.users = users = {};
  var availFrags = disk.getAvailFragmentsSync();
  for (var i = 0; i < availFrags.sizeSync(); i++) {
    var frag = availFrags.getSync(i);
    var root = frag.getRootSync();
    for (var j = 0; j < root.getSubFcbsSync().sizeSync(); j++) {
      var fcb = root.getSubFcbsSync().getSync(j);
      data.users[fcb.getNameSync()] = {
        id: fcb.getIdSync,
        folder: fcb,
        name: fcb.getNameSync()
      };
    }
  }
}

function getPcbInfo(data) {
  data.pcbs = {
    mode: pcbManager.getArithmeticStatusSync(),
    // init: pcbArray(pcbManager.getInitPCBListSync()),
    // ready: pcbArray(pcbManager.getReadyPCBListSync()),
    // wait: pcbArray(pcbManager.getWaitPCBListSync()),
    // finish: pcbArray(pcbManager.getFinishPCBListSync()),
    total: pcbArray(pcbManager.getTotalPCBListSync())
  };
}

function pcbArray(pcbList) {
  var ret = [];
  for (var i = 0; i < pcbList.sizeSync(); i++) {
    ret.push(createPcb(pcbList.getSync(i)));
  }
  return ret;
}

function createPcb(data) {
  var pcb = data;
  var pid = pcb.getpIdStringSync();
  return {
    id: pid,
    name: pcb.getNameSync(),
    cpuTime: pcb.getRunTimeSync(),
    memorySize: pcb.getMemorySync().getSizeSync(),
    level: pcb.getLevelSync(),
    status: pcb.getStatusSync(),
    user: pcb.getUserSync(),
    message: pcb.outputMessageSync()
  };
}

function getMemoryInfo(data) {
  data.memory = {
    mode: Memory.getAllocateModeSync().toStringSync(),
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
      pcbName: pcb === null ? 'idle' : pcb.getNameSync()
    });
    cur = cur.getNextSync();
  }
}

function getHardDiskInfo(data) {

  data.disk = {
    sectionSize: disk.sectionSize,
    sectionNum: disk.sectionNum
  };

  data.disk.diskSections = [];
  var sections = disk.getSectionsSync(),
      fcb;
  for (var i = 0; i < sections.sizeSync(); i++) {
    var section = sections.getSync(i);
    data.disk.diskSections.push({
      isIdle: section.isIdleSync(),
      fcb: (fcb = section.getFcbSync()) ? fcb.getIdStringSync() : null
    });
  }

  data.disk.fragments = [];
  var fragments = disk.getFragmentsSync();
  for (i = 0; i < fragments.sizeSync(); i++) {
    var fragment = fragments.getSync(i);
    data.disk.fragments.push({
      name: fragment.getNameSync(),
      size: fragment.getSizeSync(),
      id: fragment.getIdStringSync(),
      isIdle: fragment.isIdleSync()
    });
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