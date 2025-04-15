package com.github.josepaumard.bench;

// Benchmark                                Mode  Cnt     Score     Error  Units
// ArenaBenchmarks.newArray                 avgt    5     2.522 ±   0.015  ns/op
// ArenaBenchmarks.newSegmentAuto           avgt    5   434.694 ± 247.868  ns/op
// ArenaBenchmarks.newSegmentConfined       avgt    5    82.287 ±   1.530  ns/op
// ArenaBenchmarks.newSegmentShared         avgt    5  6696.144 ±  37.833  ns/op
// ArenaBenchmarks.newSegmentWrap           avgt    5     6.494 ±   0.093  ns/op
// ArenaBenchmarks.newUnsafeMemory          avgt    5    22.834 ±   0.097  ns/op
// ArenaBenchmarks.newUnsafeMemoryWithInit  avgt    5    72.338 ±   0.390  ns/op

/*
// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ArenaBenchmarks {
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

  @Benchmark
  public void newUnsafeMemory(Blackhole blackhole) {
    var memory = UNSAFE.allocateMemory(16 * 4);
    try {
      blackhole.consume(memory);
    } finally {
      UNSAFE.freeMemory(memory);
    }
  }

  @Benchmark
  public void newUnsafeMemoryWithInit(Blackhole blackhole) {
    var memory = UNSAFE.allocateMemory(16 * 4);
    try {
      UNSAFE.setMemory(memory, 16 * 4,  (byte) 0);
      blackhole.consume(memory);
    } finally {
      UNSAFE.freeMemory(memory);
    }
  }

  @Benchmark
  public void newArray(Blackhole blackhole) {
    var array = new int[16];
    blackhole.consume(array);
  }


  @Benchmark
  public void newSegmentWrap(Blackhole blackhole) {
    var array = new int[16];
    var segment = MemorySegment.ofArray(array);
    blackhole.consume(segment);
  }
  @Benchmark
  public void newSegmentShared(Blackhole blackhole) {
    try(var arena = Arena.ofShared()) {
      var segment = arena.allocate(16 * 4, 4);
      blackhole.consume(segment);
    }
  }
  @Benchmark
  public void newSegmentConfined(Blackhole blackhole) {
    try(var arena = Arena.ofConfined()) {
      var segment = arena.allocate(16 * 4, 4);
      blackhole.consume(segment);
    }
  }
  @Benchmark
  public void newSegmentAuto(Blackhole blackhole) {
    var arena = Arena.ofAuto();
    var segment = arena.allocate(16 * 4, 4);
    blackhole.consume(segment);
  }
}*/

