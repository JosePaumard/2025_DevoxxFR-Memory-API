package org.paumard.intro;

import java.io.IOException;
import java.lang.foreign.*;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

import static org.paumard.intro.Data.DIR;

public class C_RearrangeTemperaturesPerCity {

    public static final ValueLayout.OfInt CITY_ID_LAYOUT =
            ValueLayout.JAVA_INT
                    .withOrder(ByteOrder.BIG_ENDIAN)
                    .withName("city-id");

    public static final ValueLayout.OfLong TEMPERATURE_COUNT_LAYOUT =
            ValueLayout.JAVA_LONG
                    .withOrder(ByteOrder.BIG_ENDIAN)
                    .withName("temperature-count");

    public static final ValueLayout.OfFloat TEMPERATURE_LAYOUT =
            ValueLayout.JAVA_FLOAT
                    .withOrder(ByteOrder.BIG_ENDIAN)
                    .withName("temperature");

    public static final StructLayout ELEMENT_LAYOUT =
            MemoryLayout.structLayout(CITY_ID_LAYOUT, TEMPERATURE_LAYOUT);

    public static final StructLayout CITY_TEMPERATURE_COUNT_LAYOUT =
            MemoryLayout.structLayout(
                    CITY_ID_LAYOUT,
                    MemoryLayout.paddingLayout(4L),
                    TEMPERATURE_COUNT_LAYOUT);

    public static final VarHandle CITY_ID_VARHANDLE =
            ELEMENT_LAYOUT.varHandle(
                            MemoryLayout.PathElement.groupElement("city-id"))
                    .withInvokeExactBehavior();

    public static final VarHandle TEMPERATURE_VARHANDLE =
            ELEMENT_LAYOUT.varHandle(
                            MemoryLayout.PathElement.groupElement("temperature"))
                    .withInvokeExactBehavior();

    public static final VarHandle WRITE_CITY_ID_VARHANDLE =
            CITY_TEMPERATURE_COUNT_LAYOUT.varHandle(
                    MemoryLayout.PathElement.groupElement("city-id"));

    public static final VarHandle WRITE_TEMPERATURE_COUNT_VARHANDLE =
            CITY_TEMPERATURE_COUNT_LAYOUT.varHandle(
                            MemoryLayout.PathElement.groupElement("temperature-count"))
                    .withInvokeExactBehavior();

    public static final long MAX_TEMPERATURE_COUNT = 100_000L;

    public static final Path FILES_MEASUREMENTS_BIN =
          DIR.resolve("files", "measurements-1000M.bin");
    public static final Path FILES_REARRANGED_BIN =
          DIR.resolve("files", "measurements-rearranged-A.bin");

    public static void main(String... args) throws IOException {

        try (var arena = Arena.ofShared();
             var readChannel = FileChannel.open(FILES_MEASUREMENTS_BIN);
             var writeChannel =
                     FileChannel.open(FILES_REARRANGED_BIN,
                             StandardOpenOption.WRITE,
                             StandardOpenOption.CREATE);) {

            System.out.println("Openning file");

            // Reading the file
            var begin = Instant.now();
            long fileSize = readChannel.size();
            var inMemoryFile = readChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0L, fileSize, arena);

            var elementCount = fileSize / ELEMENT_LAYOUT.byteSize();
            var end = Instant.now();
            System.out.println("File read in " + Duration.between(begin, end).toMillis() + "ms");
            System.out.println("# elements read = " + elementCount);

            // Max number of temperatures
            begin = Instant.now();
            var numberOfTemperaturesPerCity =
                    inMemoryFile.elements(ELEMENT_LAYOUT)
                            .collect(
                                    Collectors.groupingBy(
                                            segment -> (int) CITY_ID_VARHANDLE.get(segment, 0L),
                                            Collectors.counting()
                                    )
                            );
            end = Instant.now();
            var max = numberOfTemperaturesPerCity.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .orElseThrow();
            System.out.println("The city " + max.getKey() + " has " + max.getValue() + " temperature");
            var min = numberOfTemperaturesPerCity.entrySet()
                    .stream()
                    .min(Map.Entry.comparingByValue())
                    .orElseThrow();
            System.out.println("The city " + min.getKey() + " has " + min.getValue() + " temperature");
            System.out.println("Computed in " + Duration.between(begin, end).toMillis() + "ms");

            // ID N_TEMPERATURES T1 T2 ... TN
            // segments of 100k temperatures
            // 12.5M temperatures per city
            class TemperaturesSegment {
                private final MemorySegment segment;
                private long index;

                TemperaturesSegment() {
                    this.segment = arena.allocate(TEMPERATURE_LAYOUT, MAX_TEMPERATURE_COUNT);
                    this.index = 0L;
                }

                public boolean isFull() {
                    return index == MAX_TEMPERATURE_COUNT;
                }

                public void add(float temperature) {
                    segment.setAtIndex(ValueLayout.OfFloat.JAVA_FLOAT, index++, temperature);
                }

                public long count() {
                    return this.index;
                }

                public long countBytes() {
                    return this.index * 4L;
                }

                public long segmentSize() {
                    return countBytes() + countBytes() % 8L;
                }

                public MemorySegment segment() {
                    return this.segment;
                }
            }

            begin = Instant.now();
            System.out.println("Creating segments");
            var cities = new HashMap<Integer, Deque<TemperaturesSegment>>();
            inMemoryFile.elements(ELEMENT_LAYOUT)
                    .forEach(segment -> {
                        var cityID = (int) CITY_ID_VARHANDLE.get(segment, 0L);
                        var temperature = (float) TEMPERATURE_VARHANDLE.get(segment, 0L);
                        cities.computeIfAbsent(cityID, _ -> new LinkedList<>());
                        var temperaturesSegment = cities.get(cityID).peek();
                        if (temperaturesSegment == null || temperaturesSegment.isFull()) {
                            temperaturesSegment = new TemperaturesSegment();
                            cities.get(cityID).push(temperaturesSegment);
                        }
                        temperaturesSegment.add(temperature);
                    });
            end = Instant.now();
            System.out.println("Computed in " + Duration.between(begin, end).toMillis() + "ms");

            // ID N_TEMPERATURES T1 T2 ... TN
            begin = Instant.now();
            var temperaturesSize = cities.values()
                    .stream().flatMapToLong(segments -> segments.stream().mapToLong(TemperaturesSegment::segmentSize))
                    .sum();
            var finalSegmentByteSize = cities.size() * (4L + 8L) + temperaturesSize * 4L;
            var finalSegment = arena.allocate(finalSegmentByteSize);

            long finalSegmentOffset = 0L;
            System.out.println("Computing final segment");
            for (var entry : cities.entrySet()) {
                var cityTemperatureCountSegment = finalSegment.asSlice(finalSegmentOffset, CITY_TEMPERATURE_COUNT_LAYOUT);
                int cityID = entry.getKey();
                long temperatureCount = entry.getValue().stream().mapToLong(TemperaturesSegment::count).sum();
                WRITE_CITY_ID_VARHANDLE.set(cityTemperatureCountSegment, 0L, cityID);
                WRITE_TEMPERATURE_COUNT_VARHANDLE.set(cityTemperatureCountSegment, 0L, temperatureCount);
                finalSegmentOffset += cityTemperatureCountSegment.byteSize();

                long temperatureSegmentBytes = temperatureCount * 4L;
                temperatureSegmentBytes += temperatureSegmentBytes % 8L;
                var temperaturesSegment = finalSegment.asSlice(finalSegmentOffset, temperatureSegmentBytes);
                var temperatureOffset = 0L;
                for (var temperatureSegment : entry.getValue()) {
                    MemorySegment.copy(temperatureSegment.segment(), 0L, temperaturesSegment, temperatureOffset, temperatureSegment.countBytes());
                    temperatureOffset += temperatureSegment.countBytes();
                }
                finalSegmentOffset += temperatureSegmentBytes;
            }
            end = Instant.now();
            System.out.println("Final segment size is " + finalSegmentOffset);
            System.out.println("Computed in " + Duration.between(begin, end).toMillis() + "ms");

            // Writing segment to file
            var writeOffset = 0L;
            while (writeOffset < finalSegmentOffset) {
                var nextBatchSize = finalSegmentOffset - writeOffset;
                nextBatchSize = nextBatchSize >= Integer.MAX_VALUE ? Integer.MAX_VALUE / 2 : nextBatchSize;
                var written = writeChannel.write(finalSegment.asSlice(writeOffset, nextBatchSize).asByteBuffer());
                writeOffset += written;
            }
            System.out.println("Written " + writeOffset + " bytes");
        }
    }
}
