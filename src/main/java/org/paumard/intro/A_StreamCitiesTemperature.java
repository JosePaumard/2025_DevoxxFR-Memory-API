package org.paumard.intro;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summarizingDouble;

public class A_StreamCitiesTemperature {

    private static final Path CITIES_PATH =
          Data.DIR.resolve("measurements-1000M.bin");

    // Reading City ID and Temperature
    // 08_stream-of-cities-temperatures

    private static final StructLayout ELEMENT_LAYOUT =
          MemoryLayout.structLayout(
                ValueLayout.JAVA_INT
                      .withOrder(ByteOrder.BIG_ENDIAN)
                      .withName("city-id"),
                ValueLayout.JAVA_FLOAT
                      .withOrder(ByteOrder.BIG_ENDIAN)
                      .withName("temperature")
          );

    private static final VarHandle CITY_ID_VARHANDLE =
          ELEMENT_LAYOUT.varHandle(
                      MemoryLayout.PathElement.groupElement("city-id"))
                .withInvokeExactBehavior();

    private static final VarHandle TEMPERATURE_VARHANDLE =
          ELEMENT_LAYOUT.varHandle(
                      MemoryLayout.PathElement.groupElement("temperature"))
                .withInvokeExactBehavior();


    void main() throws IOException {

        var begin = Instant.now();
        try (var arena = Arena.ofShared();
             var readChannel = FileChannel.open(CITIES_PATH, StandardOpenOption.READ)) {

            var fileSize = readChannel.size();
            var inMemoryFile = readChannel.map(FileChannel.MapMode.READ_ONLY, 0L, fileSize, arena);
            var end = Instant.now();
            System.out.println("Element count: " + (fileSize / ELEMENT_LAYOUT.byteSize()));
            System.out.println("Computing time: " + Duration.between(begin, end).toMillis() + " ms");

            // Counting cities

            begin = Instant.now();
            var countCities = inMemoryFile.elements(ELEMENT_LAYOUT)
                  .parallel()
                  .map(segment -> (int) CITY_ID_VARHANDLE.get(segment, 0L))
                  .distinct()
                  .count();
            end = Instant.now();

            System.out.println("# cities = " + countCities);
            System.out.println("Computing time: " + Duration.between(begin, end).toMillis() + " ms");

            // Stats on the temperatures
            begin = Instant.now();
            var stats = inMemoryFile.elements(ELEMENT_LAYOUT)
                  .parallel()
                  .collect(summarizingDouble(segment -> (float) TEMPERATURE_VARHANDLE.get(segment, 0L)));
            end = Instant.now();

            System.out.println("Average temperature = " + stats.getAverage());
            System.out.println("Computing time: " + Duration.between(begin, end).toMillis() + " ms");

            // Stats on the temperatures per city
            begin = Instant.now();
            var statsPerCity = inMemoryFile.elements(ELEMENT_LAYOUT)
                  .parallel()
                  .collect(
                        Collectors.groupingBy(
                              segment -> (int) CITY_ID_VARHANDLE.get(segment, 0L),
                              summarizingDouble(segment -> (float) TEMPERATURE_VARHANDLE.get(segment, 0L))
                        )
                  );

            var cityWithMaxTemperature =
                  statsPerCity.entrySet().stream()
                        .max(Map.Entry.comparingByValue(Comparator.comparing(DoubleSummaryStatistics::getMax)))
                        .orElseThrow();
            var cityWithMinTemperature =
                  statsPerCity.entrySet().stream()
                        .max(Map.Entry.comparingByValue(Comparator.comparing(DoubleSummaryStatistics::getMin)))
                        .orElseThrow();


            end = Instant.now();

            System.out.println("City with max temp: " + cityWithMaxTemperature.getKey() + " = " +
                  cityWithMaxTemperature.getValue().getMax());
            System.out.println("City with min temp: " + cityWithMinTemperature.getKey() + " = " +
                  cityWithMinTemperature.getValue().getMin());
            System.out.println("Computing time: " + Duration.between(begin, end).toMillis() + " ms");
        }
    }
}
