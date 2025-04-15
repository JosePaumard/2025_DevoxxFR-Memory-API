public static final class MyByteBuffer {
  private static final Cleaner CLEANER = Cleaner.create();

  private MyByteBuffer() {}

  public static MyByteBuffer allocate(int size) {
    var buffer = new MyByteBuffer();
    CLEANER.register(buffer, () -> IO.println("buffer deallocated! " + Thread.currentThread()));
    return buffer;
  }
}

void main() throws InterruptedException {
  var byteBuffer = MyByteBuffer.allocate(8192);
  byteBuffer = null;
//  IO.println("Calling System.gc()");
//  System.gc();  // require an explicit call to System.gc() !
  Thread.sleep(4_000);  // wait because the "deallocation" is done by another thread
}
