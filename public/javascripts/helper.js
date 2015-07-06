function uuidToHex(uuid) {
  var g = parseInt(uuid.substr(2, 2), 16);
  var r = parseInt(uuid.substr(0, 2), 16);
  r = Math.max(~~(r / 2), 16);
  g = Math.max(~~(g / 2), 16);
  return '#' + r.toString(16) + g.toString(16) + uuid.substr(4, 2);
}

function formatTime(n) {
  var h, m, s = n;
  if (s < 60) {
    return s;
  } else if (s < 3600) {
    m = ~~(s / 60);
    s = n - m * 60;
    return m + ':' + s;
  } else {
    h = ~~(s / 3600);
    m = ~~((s - h * 3600)/ 60);
    s = s - h * 3600 - m * 60;
    return h + ':' + m + ':' + s;
  }
}

$(document).ready(function() {

  prepareForm();

});

function update(A, B, equal, updatecb, morecb, lesscb) {
  var a, b, found;
  for(a in A) {
    found = false;
    for(b in B) {
      if (equal(a, b)) {
        found = true;
      }
    }
    if (!found) {
      lesscb(A, B, a);
      delete A[a];
    }
  }

  for(b in B) {
    found = false;
    for(a in A) {
      if (equal(a, b)) {
        found = true;
      }
    }
    if (!found) {
      A[b] = B[b];
      morecb(A, B, b);
    }
  }
}

function prepareForm() {
  $('form, [data-role="form"]').on('click', 'paper-button,  paper-icon-button', function() {
    if ($(this).attr('role') === 'ordinary') {
      return;
    }

    var form = $(this).parents('form, [data-role="form"]').eq(0);
    var action = form.attr('action');
    var data = {};
    $(form).find('paper-input,paper-textarea,input,paper-radio-group').each(function(index, val) {
      if ($(val).attr('label') || $(val).attr('name')) {
        if ($(val).is('paper-radio-group')) {
          data[$(val).attr('label')] = val.selectedItem.computedName;
        } else {
          data[$(val).attr('label')] = $(val).val();
        }
      }
    });

    console.log(data);

    socket.emit(action, data);
  });
}


function createTr(pcb) {
  var tr = $('<tr></tr>');
  tr.attr('pid', pcb.id);
  tr.append($('<td>' + pcb.name + '</td>'));
  tr.append($('<td>' + pcb.cpuTime + '</td>'));
  tr.append($('<td>' + pcb.memorySize + '</td>'));
  tr.append($('<td>' + pcb.level + '</td>'));
  tr.append($('<td>' + pcb.status + '</td>'));
  tr.append($('<td>' + pcb.message + '</td>'));
  tr.append($('<td><paper-button class="operation" label="stop">stop</paper-button>' +
    '<paper-button class="operation" label="wait">wait</paper-button>' +
    '<paper-button class="operation" label="restart">restart</paper-button>' +
    '<paper-button class="operation" label="message">message</paper-button></td>'));
  return tr;
}
