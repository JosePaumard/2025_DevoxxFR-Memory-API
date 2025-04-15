void main() {
  var byteBuffer = ByteBuffer.allocateDirect(512);
  IO.println("bytebuffer " + byteBuffer);

  // index value
  byteBuffer.putInt(4, 42);
  var value = byteBuffer.getInt(4);
  IO.println("Value = " + value);
}
