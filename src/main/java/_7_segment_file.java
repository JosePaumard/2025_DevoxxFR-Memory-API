import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

void main() throws IOException {
  var text = """
    hello byte buffer
    """;
  var segment = Arena.global().allocate(64L);

  var array = text.getBytes(UTF_8);
  segment.copyFrom(MemorySegment.ofArray(array));  // no '\0' added
  var byteBuffer = segment.asByteBuffer();
  byteBuffer.limit(array.length);

  var path = Path.of("files/file7");
  try(var file = FileChannel.open(path, CREATE, WRITE)) {
    file.write(byteBuffer);
  }

  IO.println("file: " + Files.readString(path));
}
