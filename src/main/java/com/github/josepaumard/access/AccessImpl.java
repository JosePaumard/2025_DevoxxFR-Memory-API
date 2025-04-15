package com.github.josepaumard.access;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

import static java.util.Objects.requireNonNull;

record AccessImpl(MethodHandle getInt, MethodHandle setInt) implements Access {
  private static void checkPositive(long index) {
    if (index < 0) {
      throw new IllegalArgumentException("index " + index + " is negative");
    }
  }

  @Override
  public int getAtIndex(MemorySegment segment, long index, String field) {
    requireNonNull(segment);
    requireNonNull(field);
    checkPositive(index);
    try {
      return (int) getInt.invokeExact(segment, field, index);
    } catch (Throwable e) {
      throw rethrow(e);
    }
  }

  @Override
  public void setAtIndex(MemorySegment segment, long index, String field, int value) {
    requireNonNull(segment);
    requireNonNull(field);
    checkPositive(index);
    try {
      setInt.invokeExact(segment, field, index, value);
    } catch (Throwable e) {
      throw rethrow(e);
    }
  }

  @SuppressWarnings("unchecked")
  private static <X extends Throwable> RuntimeException rethrow(Throwable throwable) throws X {
    throw (X) throwable;
  }
}