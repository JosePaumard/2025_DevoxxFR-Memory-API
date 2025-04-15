void main() {
  var segment = Arena.global().allocate(512L);
  //var segment = MemorySegment.ofArray(new int[] { 0, 1, 2, 3, 4 });
  IO.println("segment " + segment);

  segment.set(ValueLayout.JAVA_INT, 4L, 42);
//  segment.set(ValueLayout.JAVA_INT, 3L, 42);  // IAE alignment
  var value = segment.get(ValueLayout.JAVA_INT, 4L);
  IO.println(value);

  var array = Arena.global().allocate(ValueLayout.JAVA_INT, 128L);
  array.setAtIndex(ValueLayout.JAVA_INT, 4L, 777);  // indexed get
  var value2 = array.getAtIndex(ValueLayout.JAVA_INT, 4L);
  IO.println(value2);

  // global arena == global memory
}
