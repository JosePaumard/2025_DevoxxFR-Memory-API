package org.paumard.intro;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;

public class B_StreamCitiesTemperatureArray {

    private static final Path CITIES_PATH =
          Data.DIR.resolve("measurements-rearranged-1000M.bin");

    // Reading City ID, Temperature count and temperatures
    // 10_id-array-of-temperaturess-sutructure
    // 11_header-layout
    // 12_temperatures-array-segment
    // 13_temperatures-array-segment-alignment
    // 14_sub-segment

    private static final MemoryLayout HEADER_LAYOUT =
            MemoryLayout.structLayout(
                    ValueLayout.JAVA_INT
                            .withOrder(ByteOrder.BIG_ENDIAN)
                            .withName("city-id"),
                    MemoryLayout.paddingLayout(4L),
                    ValueLayout.JAVA_LONG
                            .withOrder(ByteOrder.BIG_ENDIAN)
                            .withName("temperature-count")
            );

    private static final MemoryLayout TEMPERATURE_LAYOUT =
            ValueLayout.OfFloat.JAVA_FLOAT;

    private static final VarHandle CITY_ID_VARHANDLE =
            HEADER_LAYOUT.varHandle(
                            MemoryLayout.PathElement.groupElement("city-id"))
                    .withInvokeExactBehavior();

    private static final VarHandle TEMPERATURE_COUNT_VARHANDLE =
            HEADER_LAYOUT.varHandle(
                            MemoryLayout.PathElement.groupElement("temperature-count"))
                    .withInvokeExactBehavior();

    private static final VarHandle TEMPERATURE_VARHANDLE =
            TEMPERATURE_LAYOUT.varHandle()
                    .withInvokeExactBehavior();


    void main() throws IOException {

        var begin = Instant.now();
        try (var arena = Arena.ofShared();
             var readChannel = FileChannel.open(CITIES_PATH, StandardOpenOption.READ)) {

            var fileSize = readChannel.size();
            var inMemoryFile = readChannel.map(FileChannel.MapMode.READ_ONLY, 0L, fileSize, arena);
            var end = Instant.now();
            System.out.println("Reading time: " + Duration.between(begin, end).toMillis() + " ms");

            var map = new HashMap<Integer, DoubleSummaryStatistics>();
            var offset = 0L;
            while (offset < inMemoryFile.byteSize()) {

                var cityId = (int) CITY_ID_VARHANDLE.get(inMemoryFile, offset);
                var temperatureCount = (long) TEMPERATURE_COUNT_VARHANDLE.get(inMemoryFile, offset);
                var temperatureArraySize = temperatureCount * TEMPERATURE_LAYOUT.byteSize();
                var headerSize = HEADER_LAYOUT.byteSize();
                var temperatureSegment = inMemoryFile.asSlice(headerSize, temperatureArraySize);
                var stats = temperatureSegment.elements(TEMPERATURE_LAYOUT)
                        .parallel()
                        .mapToDouble(segment -> (float) TEMPERATURE_VARHANDLE.get(segment, 0L))
                        .summaryStatistics();
                map.put(cityId, stats);

                var temperatureSliceSize = temperatureArraySize + temperatureArraySize % 8L;
                offset += headerSize + temperatureSliceSize;
            }
            var endMap = Instant.now();
            System.out.println("Computing time: " + Duration.between(end, endMap).toMillis() + " ms");
            System.out.println("# cities = " + map.size());
            System.out.println(map.get(0));
        }
    }
}
