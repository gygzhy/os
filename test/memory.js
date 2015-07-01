var java = require('java');
var path = require('path');

java.classpath.push(path.join(__dirname, '../java_util/Resource.jar'));
var Memory = java.import('com.csu.os.resource.Memory');

describe('Allocated memory', function() {
  var mem1 = Memory.AllocateSync(300);
  var mem2 = Memory.AllocateSync(20);
  var mem3 = Memory.AllocateSync(3000);

  console.log('allocate done');

  it('should be three memory section', function() {
    expect(Memory.getMemoryAllSectionNumSync()).toBe(3);
  });

  it('should have 300 in size', function() {
    expect(mem1.getSizeSync()).toBe(300);
  });

  it('should have different UUIDs', function() {
    expect(mem1.getIdSync()).not.toEqual(mem2.getIdSync());
  });

  it('should be insufficient and return null', function() {
    expect(mem3).toBeNull();
  });

  it('should be only one memory section after freeing all', function() {
    mem1.freeSync();
    mem2.freeSync();

    expect(Memory.getMemoryAllSectionNumSync()).toBe(1);
  });

  it('should be 2048 after freeing all', function() {
    expect(Memory.getIdleSizeSync()).toBe(2048);
  });

});
