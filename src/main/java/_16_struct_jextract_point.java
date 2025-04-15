import com.github.josepaumard.jextractdemo.point;

void main() {
  IO.println("layout " + point.layout());  // [i4(x)i4(y)](point)

  try(var arena = Arena.ofConfined()) {
    var segment = point.allocate(arena);
    point.x(segment, 42);
    IO.println("p.x " + point.x(segment));

    var array = point.allocateArray(16L, arena);
    point.x(point.asSlice(array, 2L), 42);
    for(var index = 0; index < 4; index++) {
      IO.println(point.x(point.asSlice(array, index)));
    }
  }
}
