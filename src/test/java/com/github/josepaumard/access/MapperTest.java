package com.github.josepaumard.access;

import com.github.josepaumard.mapper.RecordMapper;
import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandles;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapperTest {
  @Test
  public void mappingStruct() {
    var layout = MemoryLayout.structLayout(
        ValueLayout.JAVA_INT.withName("x"),
        ValueLayout.JAVA_INT.withName("y")
    );
    record Point(int x, int y) {}
    var mapper = RecordMapper.of(MethodHandles.lookup(), layout, Point.class);
    try(var arena = Arena.ofConfined()) {
      var point = arena.allocate(layout);
      mapper.set(point, new Point(42, 12));
      assertEquals(new Point(42, 12), mapper.get(point));
    }
  }

  @Test
  public void mappingArrayOfStruct() {
    var layout = MemoryLayout.structLayout(
        ValueLayout.JAVA_INT.withName("x"),
        ValueLayout.JAVA_INT.withName("y")
    );
    record Point(int x, int y) {}
    var mapper = RecordMapper.of(MethodHandles.lookup(), layout, Point.class);
    try(var arena = Arena.ofConfined()) {
      var array = arena.allocate(layout, 128L);
      for(var i = 0; i < 128; i++) {
        mapper.setAtIndex(array, i, new Point(i, i));
      }
      assertEquals(new Point(12, 12), mapper.getAtIndex(array, 12L));
    }
  }
}
