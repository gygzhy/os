var java = require('java');
var path = require('path');

java.classpath.push(path.join(__dirname, '../java_util/Resource.jar'));
var Memory = java.import('com.csu.os.resource.Memory');

describe('Allocated memory', function() {
  var mem1 = Memory.AllocateSync(300);
  var mem2 = Memory.AllocateSync(20);
  var mem3 = Memory.AllocateSync(3000);

  it('should have 300 in size', function() {
    expect(mem1.getSizeSync()).toBe(300);
  });

  it('should have different UUIDs', function() {
    expect(mem1.getIdSync()).not.toEqual(mem2.getIdSync());
  });

  it('should be exceeded and return null', function() {
    expect(mem3).toBeNull();
  });

  it('should be 300 more after freeing', function() {
    var idleSize = Memory.getIdleSizeSync();
    mem1.freeSync();
    var newIdleSize = Memory.getIdleSizeSync();

    expect(newIdleSize - idleSize).toEqual(300);
  });
});
