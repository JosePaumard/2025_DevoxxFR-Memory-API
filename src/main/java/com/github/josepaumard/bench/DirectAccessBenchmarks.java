package com.github.josepaumard.bench;

// Benchmark                                Mode  Cnt  Score   Error  Units
// DirectAccessBenchmarks.arrayAccess       avgt    5  0.728 ± 0.009  ns/op
// DirectAccessBenchmarks.unsafeAccess      avgt    5  0.627 ± 0.001  ns/op
// DirectAccessBenchmarks.ofArrayAccess     avgt    5  1.358 ± 0.003  ns/op
// DirectAccessBenchmarks.ofAutoAccess      avgt    5  1.254 ± 0.002  ns/op
// DirectAccessBenchmarks.ofConfinedAccess  avgt    5  1.255 ± 0.002  ns/op
// DirectAccessBenchmarks.ofGlobalAccess    avgt    5  1.258 ± 0.026  ns/op
// DirectAccessBenchmarks.ofSharedAccess    avgt    5  1.254 ± 0.013  ns/op

/*
// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class DirectAccessBenchmarks {
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
  private final MemorySegment global, ofAuto, ofConfined, ofShared;
  private final MemorySegment ofArray;
  {
    array = IntStream.range(0, 512).toArray();
    ofArray = MemorySegment.ofArray(array);
    global = Arena.global().allocate(ValueLayout.JAVA_INT, 512);
    global.copyFrom(ofArray);
    ofAuto = Arena.ofAuto().allocate(ValueLayout.JAVA_INT, 512);
    ofAuto.copyFrom(ofArray);
    ofConfined = Arena.ofConfined().allocate(ValueLayout.JAVA_INT, 512);
    ofConfined.copyFrom(ofArray);
    ofShared = Arena.ofShared().allocate(ValueLayout.JAVA_INT, 512);
    ofShared.copyFrom(ofArray);
  }

  private int location = 12;

  @Benchmark
  public int arrayAccess() {
    return array[location];
  }

  @Benchmark
  public int unsafeAccess() {
    return UNSAFE.getInt(array, location * 4L);
  }

  @Benchmark
  public int ofArrayAccess() {
    return ofArray.get(ValueLayout.JAVA_INT, location * 4L);
  }

  @Benchmark
  public int ofGlobalAccess() {
    return global.get(ValueLayout.JAVA_INT, location * 4L);
  }

  @Benchmark
  public int ofAutoAccess() {
    return ofAuto.get(ValueLayout.JAVA_INT, location * 4L);
  }

  @Benchmark
  public int ofConfinedAccess() {
    return ofConfined.get(ValueLayout.JAVA_INT, location * 4L);
  }

  @Benchmark
  public int ofSharedAccess() {
    return ofShared.get(ValueLayout.JAVA_INT, location * 4L);
  }
}
*/


