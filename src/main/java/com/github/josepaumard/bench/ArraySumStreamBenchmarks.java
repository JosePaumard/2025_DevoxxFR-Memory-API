package com.github.josepaumard.bench;

// Benchmark                                               (size)  Mode  Cnt     Score    Error  Units
// ArraySumStreamBenchmarks.confinedOffHeapLoop             10000  avgt    5     2.894 ±  0.008  us/op
// ArraySumStreamBenchmarks.confinedOffHeapLoop            100000  avgt    5    29.098 ±  0.214  us/op
// ArraySumStreamBenchmarks.confinedOffHeapLoop           1000000  avgt    5   291.142 ±  0.490  us/op
// ArraySumStreamBenchmarks.confinedOffHeapLoop          10000000  avgt    5  2916.159 ± 13.909  us/op
// ArraySumStreamBenchmarks.confinedOffHeapStream           10000  avgt    5     4.536 ±  0.240  us/op
// ArraySumStreamBenchmarks.confinedOffHeapStream          100000  avgt    5    42.331 ±  0.575  us/op
// ArraySumStreamBenchmarks.confinedOffHeapStream         1000000  avgt    5   419.122 ±  3.599  us/op
// ArraySumStreamBenchmarks.confinedOffHeapStream        10000000  avgt    5  4243.815 ± 27.038  us/op
// ArraySumStreamBenchmarks.sharedOffHeapParallelStream     10000  avgt    5    19.509 ±  3.074  us/op
// ArraySumStreamBenchmarks.sharedOffHeapParallelStream    100000  avgt    5    61.125 ±  2.785  us/op
// ArraySumStreamBenchmarks.sharedOffHeapParallelStream   1000000  avgt    5   125.038 ±  1.635  us/op
// ArraySumStreamBenchmarks.sharedOffHeapParallelStream  10000000  avgt    5   852.736 ± 13.658  us/op

/*
// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class ArraySumStreamBenchmarks {
  @Param({ "10000", "100000", "1000000", "10000000" })
  public int size;

  private MemorySegment confinedOffHeap, sharedOffHeap;

  @Setup(Level.Iteration)
  public void setUp() {
    var array = IntStream.range(0, size).toArray();
    var heap = MemorySegment.ofArray(array);
    confinedOffHeap = Arena.ofConfined().allocate(ValueLayout.JAVA_INT, size);
    confinedOffHeap.copyFrom(heap);
    sharedOffHeap = Arena.ofShared().allocate(ValueLayout.JAVA_INT, size);
    sharedOffHeap.copyFrom(heap);
  }

  @Benchmark
  public int confinedOffHeapLoop() {
    var sum = 0;
    for(var i = 0; i < size; i++) {
      sum += confinedOffHeap.getAtIndex(ValueLayout.JAVA_INT, i);
    }
    return sum;
  }

  @Benchmark
  public int confinedOffHeapStream() {
    return confinedOffHeap.elements(ValueLayout.JAVA_INT)
        .mapToInt(e -> e.get(ValueLayout.JAVA_INT, 0))
        .sum();
  }
  @Benchmark
  public int sharedOffHeapParallelStream() {
    return sharedOffHeap.elements(ValueLayout.JAVA_INT)
        .parallel()
        .mapToInt(e -> e.get(ValueLayout.JAVA_INT, 0))
        .sum();
  }
}
*/



