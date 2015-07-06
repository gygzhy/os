

// initial data for line graph
var data = {
  labels: [],
  datasets: [
    {
      label: 'Full memory',
      fillColor: "rgba(220,190,170,0.2)",
      strokeColor: "rgba(220,190,170,1)",
      pointColor: "rgba(220,220,220,1)",
      pointStrokeColor: "#fff",
      pointHighlightFill: "#fff",
      pointHighlightStroke: "rgba(220,220,220,1)"
    },
    {
      label: 'Idle Memory',
      fillColor: "rgba(200,220,190,0.2)",
      strokeColor: "rgba(200,220,190,1)",
      pointColor: "rgba(220,220,220,1)",
      pointStrokeColor: "#fff",
      pointHighlightFill: "#fff",
      pointHighlightStroke: "rgba(220,220,220,1)"
    }
  ]
};

var ctx = document.getElementById('memoryHistory').getContext('2d');
var memoryHistory = new Chart(ctx).Line(data, {bezierCurve: false});

ctx = document.getElementById('memoryPercent').getContext('2d');
var memoryPercent = new Chart(ctx).Pie();

var pcbTable = $('#pcbTable');

// update cpu time
document.querySelector('#cpuTime').addEventListener('value-change', function() {
  socket.emit('change cpu interval', this.value);
});

[].forEach.call(document.querySelectorAll('.change-mode paper-radio-button'), function(val) {
  val.addEventListener('click', function() {
    var type = $(this).parents('.change-mode').attr('data-mode');
    socket.emit('change mode ' + type, $(this).attr('name'));
  });
});

$('#enter-desktop').on('click', function() {
  $('#manage').hide();
  $('#desktop').fadeTo(400, 1);
  $('#login').show();
});

$('#pcbTable').on( 'click', '.operation', function() {
  var label = $(this).attr('label');
  socket.emit('pcb operation ' + label, {id: $(this).parents('tr').attr('pid')});
});

socket.on('server error', function(data) {
  console.log(data.error);
});
