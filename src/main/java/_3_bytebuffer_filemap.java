import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.*;

void main() throws IOException, InterruptedException {
  var path = Path.of("files/file3.txt");
  Files.writeString(path, """
      Hello file mapping!
      """);
  ByteBuffer byteBuffer;
  try (var file = FileChannel.open(path, CREATE, READ, WRITE)) {
    byteBuffer = file.map(READ_WRITE, 0L, file.size());
    var array = new byte[5];
    byteBuffer.get(0, array);
    println("Read from the file:" + new String(array, UTF_8));
    byteBuffer.put(5, (byte) '\n');
  }


//    file.force(false);

  IO.println("File content: " + Files.readString(path));

//  Thread.sleep(30_000);

  // the lifetime of the bytebuffer is not bounded to the lifetime of the file descriptor
}
