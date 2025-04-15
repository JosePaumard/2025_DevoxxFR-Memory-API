void main() {
  try(var arena = Arena.ofConfined()) {
    var segment = arena.allocate(ValueLayout.JAVA_INT,8_000_000_000L);
    /*for(var i = 0L; i < 10_000_000_000L; i++) {
      segment.setAtIndex(ValueLayout.JAVA_INT, i, Math.clamp(i, 0, Integer.MAX_VALUE));
    }*/

    var startMax = System.currentTimeMillis();
    var max =
        segment.elements(ValueLayout.JAVA_INT)
            //.parallel()
            .mapToInt(s -> s.get(ValueLayout.JAVA_INT, 0))
            .max();
    var endMax = System.currentTimeMillis();
    IO.println(max + " in " + (endMax - startMax) + "ms");
  }
}
