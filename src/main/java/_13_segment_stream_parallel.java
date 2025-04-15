void main() {
  try (var arena = Arena.ofConfined()) {
      var segment = arena.allocate(ValueLayout.JAVA_INT, 4_000_000_000L);
    /*for(var i = 0L; i < 10_000_000_000L; i++) {
      segment.setAtIndex(ValueLayout.JAVA_INT, i, Math.clamp(i, 0, Integer.MAX_VALUE));
    }*/
    var starComputation = Instant.now();
    var max =
          segment.elements(ValueLayout.JAVA_INT)
//                  .parallel()
                .mapToInt(s -> s.get(ValueLayout.JAVA_INT, 0))
                .max();
    var endComputation = Instant.now();
    IO.println(max + " in " + Duration.between(starComputation, endComputation).toMillis() + "ms");
  }
}
