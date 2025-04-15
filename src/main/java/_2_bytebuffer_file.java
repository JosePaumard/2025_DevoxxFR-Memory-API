import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

void main() throws IOException {
  var text = """
      Hello Byte Buffer!
      """;
  var byteBuffer = ByteBuffer.allocateDirect(64);
  byteBuffer.put(text.getBytes(UTF_8));
  byteBuffer.flip();

  var path = Path.of("files/file2.txt");
  try(var file = FileChannel.open(path, CREATE, WRITE)) {
    file.write(byteBuffer);
  }

  IO.println("File: " + Files.readString(path));

  // - direct ByteBuffer for allocating off-heap
  // - ByteBuffer API is I/O oriented (a position/limit/reset cursor)
  // - size limited to 32 bits
  // - no precise deallocation
}
