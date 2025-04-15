void main() {
  try(var arena1 = Arena.ofConfined()) {
    try(var arena2 = Arena.ofConfined()) {
      var segment1 = arena1.allocate(64);
      var segment2 = arena2.allocate(64);
      var segment3 = arena1.allocate(64);

      IO.println(segment1);
      IO.println(segment2);
      IO.println(segment3);
    }

    // fragmentation here !!
  }
}
