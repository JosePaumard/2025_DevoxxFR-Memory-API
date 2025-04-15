void main() {
  var segment = Arena.global().allocate(64L);
  IO.println("segment " + segment);
  IO.println("address " + segment.address());

  var address = segment.address();
  var segment2 = MemorySegment.ofAddress(address);
  IO.println(segment2);

  // IO.println(MemorySegment.NULL);
  // IO.println(MemorySegment.NULL.get(ValueLayout.JAVA_BYTE, 0));

  var segment3 = segment2.reinterpret(128L);  // requires --enable-native-access=ALL-UNNAMED
  IO.println(segment3);
}
