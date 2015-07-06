var socket = io.connect('http://localhost:3000');

$(function() {
  socket.on('error', function(data) {
    conosle.log(data.error);
  });

  socket.on('heart beat', function(data) {

    updateMemory(data.memory, data.seconds);
    updatePcbTable(data.pcbs);
    updateHardDisk(data.disk);
    updateUser(data.users);

  });

  function updateMemory(memory, seconds) {
    if (!(memoryHistory && memoryPercent)) {
      return;
    }
    // update line chart
    if (memoryHistory.datasets[0].points.length > 15) {
      memoryHistory.removeData();
    }
    memoryHistory.addData([memory.busySize, memory.idleSize],
      formatTime(seconds));
    memoryHistory.update();

    var delta = memoryPercent.segments.length - memory.sections.length;
    var i;
    if (delta > 0) {
      for(i = 0; i < delta; i++) {
        memoryPercent.removeData();
      }
    } else if(delta < 0) {
      for(i = 0; i < -delta; i++) {
        memoryPercent.addData({
          value: 0,
          color: '#eee',
          highlight: '#f8f8f8',
          label: 'idle'
        });
      }
    }

    for(i = 0; i < memory.sections.length; i++) {
      memoryPercent.segments[i].value = memory.sections[i].size;
      memoryPercent.segments[i].fillColor = memory.sections[i].isIdle ? "#eee" : uuidToHex(memory.sections[i].id);
      memoryPercent.segments[i].label = memory.sections[i].pcbName;
    }

    memoryPercent.update();
  }

  function updatePcbTable(pcbs) {
    if (!(pcbTable)) {
      return;
    }

    pcbTable.html("");
    pcbs.total.forEach(function(val) {
      pcbTable.append(createTr(val));
    });


    [].forEach.call(pcbs.total, function(val) {
      var circle = findCircle(val.id);
      if (!circle) {
        moveTo(createCircle(val), val.status);
      } else {

        if (circle.status === val.status) {
          return;
        }
        moveTo(circle, val.status);
      }
    });
  }


  var ready = [], init = [], finish = [], wait = [];
  var readyQueue = $('#ready-ground');
  var initQueue = $('#init-ground');
  var waitQueue = $('#wait-ground');
  var finishQueue = $('#finish-ground');
  var executing = null;
  var playground = $('#play-ground');
  var playgroundXHalf = playground.width() / 2;
  var playgroundYHalf = playground.height() / 2;

  socket.on('add a process', function(data) {
    var circle = createCircle(data);
    moveTo(circle, 0);
  });

  var largestSize = 0;

  function moveTo(circle, queue) {
    var from = circle.queue;
    if (from && circle.status !== 2) {
      from.splice(from.indexOf(circle), 1);
      circle.element.remove();
    }
    switch(queue) {
      case 1:
        ready.push(circle);
        circle.from = ready;
        circle.fromQueue = readyQueue;
        readyQueue.append(circle.element);
        circle.element.css('background', '#2196F3');
        circle.status = 1;
        break;
      case 0:
        init.push(circle);
        circle.from = init;
        circle.fromQueue = initQueue;
        initQueue.append(circle.element);
        circle.element.css('background', '#9FA8DA');
        circle.status = 0;
        break;
      case 4:
        finish.push(circle);
        resize(circle, 30);
        circle.element.css('background', '#aaa');
        circle.from = finish;
        circle.fromQueue = finishQueue;
        finishQueue.append(circle.element);
        circle.status = 4;
        break;
      case 3:
        wait.push(circle);
        circle.from = wait;
        circle.fromQueue = waitQueue;
        waitQueue.append(circle.element);
        circle.element.css('background', '#8D6E63');
        circle.status = 3;
        break;
      case 2:
        ready.push(circle);
        circle.from = ready;
        circle.fromQueue = readyQueue;
        readyQueue.append(circle.element);
        circle.element.css('background', '#FF9800');
        circle.status = 2;
        break;
    }
  }

  function translate(circle, to) {
    circle.element.css({
      transform: 'translate(' + to.x + 'px,' + to.y + 'px' + ')'
    });
  }

  function resize(circle, size) {
    largestSize = Math.max(largestSize, size);

    circle.element.css({
      width: size,
      height: size,
      borderRadius: size,
      lineHeight: size + 'px',
      transform: 'translate(' + circle.x + (size - circle.size)/2 + 'px, ' +
        circle.y + (size - circle.size)/2 + 'px' + ')'
    });

  }

  function findCircle(id) {
    if(executing && executing.id === id) {
      return executing;
    }

    for(var i = 0 ; i< ready.length; i++) {
      if (ready[i].id == id)
        return ready[i];
    }

    for(i = 0 ; i< init.length; i++) {
      if (init[i].id == id)
        return init[i];
    }

    for(i = 0 ; i< finish.length; i++) {
      if (finish[i].id == id)
        return finish[i];
    }

    for(i = 0 ; i< wait.length; i++) {
      if (wait[i].id == id)
        return wait[i];
    }
    return null;
  }

  function createCircle(data) {
    var circle = {};
    circle.data = data;
    circle.name = data.name;
    circle.id = data.id;
    circle.level = data.level;
    circle.runtime = data.runtime;
    circle.x = 0; circle.y = 0;
    circle.status = data.status;
    circle.element = $('<div class="play-circle">'+ circle.name +'</div>');

    if (circle.status == 2) {
        circle.element.css('background', '#99fe78');
    }
    resize(circle, circle.runtime);
    return circle;
  }



  function updateUser(newusers) {
    var user, newuser, found, users = $('#user-list span');
    users.each(function(index, user) {
      found = false;
      found = newusers[$(user).attr('name')] !== undefined;
      if (!found) {
        user.remove();
      }
    });

    for(newuser in newusers) {
      found = false;
      found = users.filter(function(index, val) {
        return $(val).attr('name') === newuser;
      }).length > 0;
      if (!found) {
        $('#user-list').append($('<span name="'+ newuser +'">' +
          newuser + '<span>'));
      }
    }
  }

  function updateHardDisk(disk) {

    var i, container = $('#disk-section-container');
    container.html('');
    for (i =0 ;i < disk.diskSections.length; i ++) {
      var sec = $('<div></div>');
      sec.addClass('disk-section');
      if (!disk.diskSections[i].isIdle) {
        sec.css('background', uuidToHex(disk.diskSections[i].fcb));
      }
      container.append(sec);
    }


    $('#disk-fragment-container').html('');
    for(i = 0; i < disk.fragments.length; i ++) {
      var frag = $('<div class="disk-fragment">' + disk.fragments[i].name + '</div>');
      frag.css({
        width: disk.fragments[i].size / disk.sectionNum * 100+ '%',
        background: uuidToHex(disk.fragments[i].id)
      });
      if (disk.fragments[i].isIdle) {
        frag.css({
          'background': '#eee',
          'color': '#000'
        });
      }
      $('#disk-fragment-container').append(frag);
    }
  }
});
