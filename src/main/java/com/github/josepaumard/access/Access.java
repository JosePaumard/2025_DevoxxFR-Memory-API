package com.github.josepaumard.access;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;

import static com.github.josepaumard.access.InlineCache.Accessor.GET;
import static com.github.josepaumard.access.InlineCache.Accessor.SET;
import static com.github.josepaumard.access.InlineCache.createMH;
import static java.util.Objects.requireNonNull;

public sealed interface Access permits AccessImpl {
  default int get(MemorySegment segment, String access) {
    return getAtIndex(segment, 0L, access);
  }
  int getAtIndex(MemorySegment segment, long index, String access);
  default void set(MemorySegment segment, String access, int value) {
    setAtIndex(segment, 0L, access, value);
  }
  void setAtIndex(MemorySegment segment, long index, String access, int value);

  static Access of(MemoryLayout layout) {
    requireNonNull(layout);
    return new AccessImpl(
        createMH(layout, int.class, GET), createMH(layout, int.class, SET));
  }
}
