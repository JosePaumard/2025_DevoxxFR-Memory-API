package com.github.josepaumard.arena;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public final class PointerBumpArena implements Arena {
  private final Arena arena;
  private MemorySegment page;
  private long top;

  public PointerBumpArena(long pageSize) {
    if (pageSize % 8 != 0) {
      throw new IllegalArgumentException("pageSize must be a multiple of 8");
    }
    var arena = Arena.ofConfined();
    this.arena = arena;
    page = arena.allocate(pageSize);
  }

  @Override
  public MemorySegment allocate(long byteSize, long byteAlignment) {
    var pageSize = page.byteSize();
    if (byteSize > pageSize / 4) {
      return arena.allocate(byteSize, byteAlignment);
    }
    if (top + byteSize > pageSize) {
      page = arena.allocate(pageSize);
    }
    var segment = page.asSlice(top, byteSize);
    top += byteSize + 8 - (byteSize % 8);
    return segment;
  }

  @Override
  public MemorySegment.Scope scope() {
    return arena.scope();
  }

  @Override
  public void close() {
    arena.close();
  }
}
