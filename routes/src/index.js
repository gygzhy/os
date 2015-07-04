import express from 'express';
import java from 'java';
import path from 'path';

const router = express.Router();

// pushing java path
const javaPath = path.join(__dirname, '..', 'java_util');
java.classpath.push(path.join(javaPath, 'os.jar'));

const log = console.log;

const Memory = java.import('com.csu.os.resource.Memory');
const cpu = new java.import('com.csu.os.managers.CPUManager')();
const pcbManager = cpu.getPcbManagerSync();
const Parameter = java.import('com.csu.os.tools.Parameter');
const Disk = java.import('com.csu.os.resource.Disk');
const FCB = java.import('com.csu.os.resource.FCB');

var disk = new Disk(2048, 32);

cpu.start();
// counting the time
var seconds = 0;

/* GET home page. */
router.get('/', function(req, res, next) {

  initIo(req.app.io);
  res.render('index', { title: 'Memory fuck hey' });
});

var ioInitialized = false;
function initIo(io) {
  if (ioInitialized) return;

  var j = 0;
  var memorys = [];
  var interval = 1000;

  io.on('connection', function(socket) {
    log('a client connet through socket');

    socket.on('allocate memory', function(data) {
      if (isFinit(data))
        var mem = Memory.Allocate(data);
      socket.emit('allocate memory', mem.getIdSync());
    });

    socket.on('change allocate mode', function(data) {

    });

    socket.on('change cpu interval', function(data) {
      console.log(`interval change to ${data}`);
      var data = data - (data % 1000);
      interval = data;
      Parameter.SLEEP_TIME = data;
    });

    socket.on('add a process', function(data) {
      console.log('a process is about to be created');
      var pcb = null
      try {
        pcb = pcbManager.addPCBSync(data.name, data.user, parseInt(data.memory), parseInt(data.level)
          , parseInt(data.time));
      } catch(e) {
        console.log("adding pcb fail" + e);
        return;
      }

      socket.emit('add a process', createPcb(pcb));

    });

    socket.on('change mode memory', function(data) {
      console.log('change mode memory');

      Memory.setAllocateModeSync(Memory.mode[data]);
    });

    socket.on('change mode cpu', function(data) {
      console.log('change mode cpu');

      pcbManager.setArithmeticStatusSync(parseInt(data));

    });

    socket.on('user login',function(data) {
      console.log(`user ${data} login`);
    });

    socket.on('user logout', function(data) {

    });

    socket.on('new fcb', function(data) {
      var fcb = new FCB(disk);
      fcbs.push(fcb);

      fcb.setNameSync(data.name);
      fcb.replaceSync(data.content);

      console.log(fcb.getIdStringSync());
      socket.emit('new fcb', {
        id: fcb.getIdStringSync(),
        name: fcb.getNameSync(),
        size: fcb.getSizeSync()
      });
    });

    socket.on('edit fcb', function(data) {
      var fcb = getFcb(data.id);
      fcb.replaceSync(data.content);
    });

    socket.on('rename fcb', function(data) {
      var fcb = getFcb(data.id);
      fcb.setNameSync(data.name);
    });

    socket.on('delete fcb', function(data) {
      var fcb = getFcb(data);
      fcb.deleteSync();
      fcbs.splice(fcbs.indexOf(fcb), 1);
    });

    socket.on('read fcb', function(data) {
      var fcb = getFcb(data);

      socket.emit('read fcb', {
        id: fcb.getIdSync(),
        content: fcb.readStringSync()
      });
    });

    socket.on('read fcb', function(data) {
      var fcb = getFcb(data);

      socket.emit('read fcb', fcb.readStringSync());
    });

    socket.on('pcb operation stop', function(data) {
      pcbManager.deletePCBSync(data);
    });

    socket.on('pcb operation restart', function(data) {
      pcbManager.activatePCBSync(data);
    });

    socket.on('pcb operation wait', function(data) {
      pcbManager.hangPCBSync(data);
    });

  });

  function getFcb(id) {
    for(var i =0 ; i <fcbs.length; i++) {
      if (id == fcbs[i].getIdSync()) {
        return fcbs[i];
      }
    }
  }

  var timer = function() {
    var data = {};
    data.seconds = seconds;

    // get the pcb infomation from java
    getPcbInfo(data);
    // get the memory informatino from java
    getMemoryInfo(data);

    getHardDiskInfo(data);
    // increase counting
    seconds += interval / 1000;

    io.emit('heart beat', data);

    setTimeout(timer, interval);
  };

  timer();

  ioInitialized = true;
}

var fcbs = [];

function getPcbInfo(data) {
  data.pcbs = {
    mode: pcbManager.getArithmeticStatusSync(),
    init: pcbArray(pcbManager.getInitPCBListSync()),
    ready: pcbArray(pcbManager.getReadyPCBListSync()),
    wait: pcbArray(pcbManager.getWaitPCBListSync()),
    finish: pcbArray(pcbManager.getFinishPCBListSync()),
    total: pcbArray(pcbManager.getTotalPCBListSync())
  };
}

function pcbArray(pcbList) {
  var ret = [];
  for(var i = 0; i < pcbList.sizeSync(); i++) {
    ret.push(createPcb(pcbList.getSync(i)));
  }
  return ret;
}

function createPcb(data) {
  let pcb = data;
  let pid = pcb.getpIdStringSync();
  return {
    id: pid,
    name: pcb.getNameSync(),
    cpuTime: pcb.getRunTimeSync(),
    memorySize: pcb.getMemorySync().getSizeSync(),
    level: pcb.getLevelSync(),
    status: pcb.getStatusSync(),
    user: pcb.getUserSync()
  };
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
  for(var i = 0; i < Memory.getMemoryAllSectionNumSync(); i++) {
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

function getHardDiskInfo(data) {

  data.disk = {
    sectionNum: disk.sectionNum,
    sectionSize: disk.sectionSize
  }

  data.disk.diskSections = [];
  var storage = disk.getStorageSync(), fcb;
  for(var i = 0; i < storage.sizeSync(); i++) {
    var section = storage.getSync(i);
    data.disk.diskSections.push({
      isIdle: section.isIdleSync(),
      fcb: (fcb = section.getFcbSync()) ? fcb.getIdStringSync() : null
    });
  }


}

process.on('exit', function(code) {
  cpu.stop();
  console.log('About to exit with code:', code);
});

process.on('SIGINT', function() {
  console.log("interrupt signal");
  process.exit();
});

module.exports = router;
