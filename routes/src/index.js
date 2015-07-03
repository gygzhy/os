import express from 'express';
import java from 'java';
import path from 'path';

const router = express.Router();

// pushing java path
const javaPath = path.join(__dirname, '..', 'java_util');
java.classpath.push(path.join(javaPath, 'os.jar'));

const log = console.log;
const Memory = java.import('com.csu.os.resource.Memory');

// counting the time
var seconds = 0;

/* GET home page. */
router.get('/', function(req, res, next) {

  initIo(req.app.io);
  res.render('index', { title: 'Memory test' });
});

var ioInitialized = false;
function initIo(io) {
  if (ioInitialized) return;

  io.on('connection', function(socket) {
    log('a client connet through socket');

    socket.on('allocate memory', function(data) {
      if (isFinit(data))
        var mem = Memory.Allocate(data);
      socket.emit('allocate memory', mem.getIdSync());
    });

    socket.on('change allocate mode', function(data) {

    });
  });

  var j = 0;
  var memorys = [];
  var timer = setInterval(function() {
    memorys.push(Memory.AllocateSync(50));
    if ((j++ % 2)) {
      memorys.shift().freeSync();
    }
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
    for(var i = 0; i < Memory.getMemoryAllSectionNumSync(); i++) {
      data.memory.sections.push({
        id: cur.getIdSync(),
        size: cur.getSizeSync(),
        isIdle: cur.isIdleSync()
      });
      cur = cur.getNextSync();
    }

    // increase counting
    seconds ++;

    io.emit('heart beat', data);
    log('heart beat');
  }, 1000);

  ioInitialized = true;
}

module.exports = router;
