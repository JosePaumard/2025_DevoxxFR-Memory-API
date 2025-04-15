import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.*;

void main() throws IOException {
  var path = Path.of("files/file7");
  Files.writeString(path, """
      Hello file mapping
      """);
  MemorySegment segment;
  try(var file = FileChannel.open(path, CREATE, READ, WRITE);
      var arena = Arena.ofConfined()) {
    segment = file.map(READ_WRITE, 0L, file.size(), arena);
    //var text = segment.getString(0, UTF_8); require a zero-ended string (like in C)

    // 1
    var arraySegment = MemorySegment.ofArray(new byte[10]);
    MemorySegment.copy(segment, 0L, arraySegment, 0L, 10L);
    // or arraySegment.copyFrom(segment.asSlice(0L, 10L));
    IO.println(new String((byte[]) arraySegment.heapBase().orElseThrow(), UTF_8));

    // 2
    var array = segment.asSlice(0L, 10L).toArray(ValueLayout.JAVA_BYTE);
    IO.println(new String(array, UTF_8));

    segment.set(ValueLayout.JAVA_BYTE, 5L, (byte) '\n');
    segment.force();
  }

  //  segment.set(ValueLayout.JAVA_BYTE, 5L, (byte) '\n');

  IO.println("file " + Files.readString(path));

  // the lifetime of the segment == lifetime of the file descriptor
}
