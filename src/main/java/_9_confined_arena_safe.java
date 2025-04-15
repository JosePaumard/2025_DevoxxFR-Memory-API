void main() throws InterruptedException {
  MemorySegment segment;
  try(var arena = Arena.ofConfined()) {
    segment = arena.allocate(ValueLayout.JAVA_INT, 512L);
    //segment.setAtIndex(ValueLayout.JAVA_INT, 1024L, 42);  // IOOBE

    new Thread(() -> {
      //segment.setAtIndex(ValueLayout.JAVA_INT, 12L, 42);  // WTE
    }).start();
    Thread.sleep(1_000);
  }
  //segment.setAtIndex(ValueLayout.JAVA_INT, 12L, 42);  // ISE
  //segment.getAtIndex(ValueLayout.JAVA_INT, 12L);  // ISE
}
