void main() {
  try(var arena = Arena.ofConfined()) {
    var segment = arena.allocate(512L);
    segment.set(ValueLayout.JAVA_INT, 4L, 42);
    var value = segment.get(ValueLayout.JAVA_INT, 4);  // raw get
    IO.println(value);

    var array = arena.allocate(ValueLayout.JAVA_INT, 512L);
    array.setAtIndex(ValueLayout.JAVA_INT, 4L, 777);  // indexed get
    var value2 = array.getAtIndex(ValueLayout.JAVA_INT, 4L);
    IO.println(value2);
  }
}
