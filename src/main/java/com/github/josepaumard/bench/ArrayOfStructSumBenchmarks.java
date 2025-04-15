package com.github.josepaumard.bench;

// Benchmark                                       Mode  Cnt    Score   Error  Units
// ArrayOfStructSumBenchmarks.arrayLoop            avgt    5  139.727 ± 0.803  ns/op
// ArrayOfStructSumBenchmarks.globalOffsetLoop     avgt    5  212.881 ± 3.436  ns/op
// ArrayOfStructSumBenchmarks.globalVarHandleLoop  avgt    5  141.190 ± 0.107  ns/op
// ArrayOfStructSumBenchmarks.heapOffsetLoop       avgt    5  214.479 ± 1.712  ns/op
// ArrayOfStructSumBenchmarks.heapVarHandleLoop    avgt    5  141.404 ± 0.081  ns/op
// ArrayOfStructSumBenchmarks.unsafeLoop           avgt    5  137.518 ± 0.476  ns/op

/*
// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ArrayOfStructSumBenchmarks {
  private static final Unsafe UNSAFE;
  static {
    Unsafe unsafe;
    try {
      var unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
      unsafeField.setAccessible(true);
      unsafe = (Unsafe) unsafeField.get(null);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
    UNSAFE = unsafe;
  }

  private record Point(int x, int y) {}

  private static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
      ValueLayout.JAVA_INT.withName("x"),
      ValueLayout.JAVA_INT.withName("y")
  );

  private static final long SIZEOF = LAYOUT.byteSize();
  private static final long OFFSET_X = LAYOUT.byteOffset(groupElement("x"));
  private static final long OFFSET_Y = LAYOUT.byteOffset(groupElement("y"));

  private static final VarHandle VH_X = LAYOUT.arrayElementVarHandle(groupElement("x"))
      .withInvokeExactBehavior();
  private static final VarHandle VH_Y = LAYOUT.arrayElementVarHandle(groupElement("y"))
      .withInvokeExactBehavior();

  //private final Point[] arrayOfObject;
  private final int[] array;
  private final MemorySegment globalOffHeap; //, autoOffHeap, confinedOffHeap, sharedOffHeap;
  private final MemorySegment heap;
  {
    //arrayOfObject = IntStream.range(0, 512).mapToObj(i -> new Point(i, i)).toArray(Point[]::new);
    array = new int[512 * (int) SIZEOF / (int) ValueLayout.JAVA_INT.byteSize()];
    heap = MemorySegment.ofArray(array);
    for(var i = 0; i < 512; i++) {
      heap.set(ValueLayout.JAVA_INT, i * SIZEOF + OFFSET_X, i);
      heap.set(ValueLayout.JAVA_INT, i * SIZEOF + OFFSET_Y, i);
    }
    globalOffHeap = Arena.global().allocate(LAYOUT, 512);
    globalOffHeap.copyFrom(heap);
    //autoOffHeap = Arena.ofAuto().allocate(LAYOUT, 512);
    //autoOffHeap.copyFrom(heap);
    //confinedOffHeap = Arena.ofConfined().allocate(LAYOUT, 512);
    //confinedOffHeap.copyFrom(heap);
    //sharedOffHeap = Arena.ofShared().allocate(LAYOUT, 512);
    //sharedOffHeap.copyFrom(heap);
  }

  @Benchmark
  public int arrayLoop() {
    var sum = 0;
    for(var i = 0; i < 512; i++) {
      var x = array[i * 2];
      var y = array[i * 2 + 1];
      sum += x + y;
    }
    return sum;
  }

  @Benchmark
  public int unsafeLoop() {
    var sum = 0;
    for(var i = 0; i < 512; i++) {
      var x = UNSAFE.getInt(array, i * 4);
      var y = UNSAFE.getInt(array, i * 4 + 4);
      sum += x + y;
    }
    return sum;
  }

  @Benchmark
  public int heapOffsetLoop() {
    var sum = 0;
    for(var i = 0; i < 512; i++) {
      var x = heap.get(ValueLayout.JAVA_INT, i * SIZEOF + OFFSET_X);
      var y = heap.get(ValueLayout.JAVA_INT, i * SIZEOF + OFFSET_Y);
      sum += x +y;
    }
    return sum;
  }

  @Benchmark
  public int heapVarHandleLoop() {
    var sum = 0;
    for(var i = 0; i < 512; i++) {
      var x = (int) VH_X.get(heap, 0L, (long) i);
      var y = (int) VH_Y.get(heap, 0L, (long) i);
      sum += x +y;
    }
    return sum;
  }

  @Benchmark
  public int globalOffsetLoop() {
    var sum = 0;
    for(var i = 0; i < 512; i++) {
      var x = globalOffHeap.get(ValueLayout.JAVA_INT, i * SIZEOF + OFFSET_X);
      var y = globalOffHeap.get(ValueLayout.JAVA_INT, i * SIZEOF + OFFSET_Y);
      sum += x +y;
    }
    return sum;
  }

  @Benchmark
  public int globalVarHandleLoop() {
    var sum = 0;
    for(var i = 0; i < 512; i++) {
      var x = (int) VH_X.get(globalOffHeap, 0L, (long) i);
      var y = (int) VH_Y.get(globalOffHeap, 0L, (long) i);
      sum += x +y;
    }
    return sum;
  }
}
*/


