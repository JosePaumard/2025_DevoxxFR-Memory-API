void main() {
  var arena = Arena.ofAuto();

  var segment = arena.allocate(512);
  segment.set(ValueLayout.JAVA_INT, 4L, 42);
  var value = segment.get(ValueLayout.JAVA_INT, 4);  // raw get
  IO.println(value);

  var segment2 = arena.allocate(ValueLayout.JAVA_INT, 512);
  segment2.setAtIndex(ValueLayout.JAVA_INT, 4L, 777);  // indexed get
  var value2 = segment2.getAtIndex(ValueLayout.JAVA_INT, 4L);
  IO.println(value2);
}
