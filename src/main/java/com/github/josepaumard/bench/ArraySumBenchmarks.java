package com.github.josepaumard.bench;

// Benchmark                               Mode  Cnt    Score   Error  Units
// ArraySumBenchmarks.arrayLoop            avgt    5  128.338 ± 0.084  ns/op
// ArraySumBenchmarks.autoOffHeapLoop      avgt    5  131.832 ± 0.491  ns/op
// ArraySumBenchmarks.confinedOffHeapLoop  avgt    5  131.829 ± 0.077  ns/op
// ArraySumBenchmarks.globalOffHeapLoop    avgt    5  131.727 ± 0.137  ns/op
// ArraySumBenchmarks.heapLoop             avgt    5  131.927 ± 0.761  ns/op
// ArraySumBenchmarks.sharedOffHeapLoop    avgt    5  131.760 ± 0.068  ns/op
// ArraySumBenchmarks.unsafeLoop           avgt    5  128.083 ± 0.131  ns/op

/*
// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ArraySumBenchmarks {
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

  private final int[] array;
  private final MemorySegment globalOffHeap, autoOffHeap, confinedOffHeap, sharedOffHeap;
  private final MemorySegment heap;
  {
    array = IntStream.range(0, 512).toArray();
    heap = MemorySegment.ofArray(array);
    globalOffHeap = Arena.global().allocate(ValueLayout.JAVA_INT, 512);
    globalOffHeap.copyFrom(heap);
    autoOffHeap = Arena.ofAuto().allocate(ValueLayout.JAVA_INT, 512);
    autoOffHeap.copyFrom(heap);
    confinedOffHeap = Arena.ofConfined().allocate(ValueLayout.JAVA_INT, 512);
    confinedOffHeap.copyFrom(heap);
    sharedOffHeap = Arena.ofShared().allocate(ValueLayout.JAVA_INT, 512);
    sharedOffHeap.copyFrom(heap);
  }

  @Benchmark
  public int arrayLoop() {
    var sum = 0;
    for(var i = 0; i < 512; i++) {
      sum += array[i];
    }
    return sum;
  }

  @Benchmark
  public int unsafeLoop() {
    var sum = 0;
    for(var i = 0; i < 512; i++) {
      sum += UNSAFE.getInt(array, i * 4);
    }
    return sum;
  }

  @Benchmark
  public int heapLoop() {
    var sum = 0;
    for(var i = 0; i < 512; i++) {
      sum += heap.getAtIndex(ValueLayout.JAVA_INT, i);
    }
    return sum;
  }

  @Benchmark
  public int globalOffHeapLoop() {
    var sum = 0;
    for(var i = 0; i < 512; i++) {
      sum += globalOffHeap.getAtIndex(ValueLayout.JAVA_INT, i);
    }
    return sum;
  }

  @Benchmark
  public int autoOffHeapLoop() {
    var sum = 0;
    for(var i = 0; i < 512; i++) {
      sum += autoOffHeap.getAtIndex(ValueLayout.JAVA_INT, i);
    }
    return sum;
  }

  @Benchmark
  public int confinedOffHeapLoop() {
    var sum = 0;
    for(var i = 0; i < 512; i++) {
      sum += confinedOffHeap.getAtIndex(ValueLayout.JAVA_INT, i);
    }
    return sum;
  }

  @Benchmark
  public int sharedOffHeapLoop() {
    var sum = 0;
    for(var i = 0; i < 512; i++) {
      sum += sharedOffHeap.getAtIndex(ValueLayout.JAVA_INT, i);
    }
    return sum;
  }
}
 */


