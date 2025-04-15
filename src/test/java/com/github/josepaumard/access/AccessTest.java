package com.github.josepaumard.access;

import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccessTest {
  @Test
  public void accessStruct() {
    var layout = MemoryLayout.structLayout(
        ValueLayout.JAVA_INT.withName("x"),
        ValueLayout.JAVA_INT.withName("y")
    );
    var access = Access.of(layout);
    try(var arena = Arena.ofConfined()) {
      var point = arena.allocate(layout);
      access.set(point, "x", 42);
      assertEquals(42, access.get(point, "x"));
    }
  }

  @Test
  public void accessArrayOfStruct() {
    var layout = MemoryLayout.structLayout(
        ValueLayout.JAVA_INT.withName("x"),
        ValueLayout.JAVA_INT.withName("y")
    );
    var access = Access.of(layout);
    try(var arena = Arena.ofConfined()) {
      var array = arena.allocate(layout, 128L);
      for(var i = 0; i < 128; i++) {
        access.setAtIndex(array, i, "x", i * 2);
      }
      assertEquals(24, access.getAtIndex(array, 12L, "x"));
    }
  }
}