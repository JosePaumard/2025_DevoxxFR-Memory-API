package com.github.josepaumard.access;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.util.Map;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;
import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.methodType;
import static java.util.Objects.requireNonNull;

class InlineCache extends MutableCallSite {
  private static final MethodHandle FALLBACK, STRING_CHECK;

  static {
    var lookup = MethodHandles.lookup();
    try {
      FALLBACK = lookup.findVirtual(InlineCache.class, "fallback",
          methodType(MethodHandle.class, MemorySegment.class, String.class));
      STRING_CHECK = lookup.findStatic(InlineCache.class, "stringCheck",
          methodType(boolean.class, MemorySegment.class, String.class, String.class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  private final MemoryLayout layout;
  private final MethodHandle support;

  InlineCache(MethodType methodType, MemoryLayout layout, MethodHandle support) {
    this.layout = layout;
    this.support = support;
    super(methodType);
    setTarget(foldArguments(exactInvoker(methodType), FALLBACK.bindTo(this)));
  }

  private static boolean stringCheck(MemorySegment unused, String s1, String s2) {
    return s1 == s2;
  }

  private MethodHandle fallback(MemorySegment segment, String field) {
    requireNonNull(segment, "segment is null");
    requireNonNull(field, "field is null");
    if (field != field.intern()) {
      throw new IllegalArgumentException("field is not a constant");
    }
    var sizeof = layout.byteSize();
    var offset = layout.byteOffset(groupElement(field));
    var mh = dropArguments(insertArguments(support, 0, sizeof, offset), 1, String.class);
    var test = insertArguments(STRING_CHECK, 1, field);
    var fallback = new InlineCache(type(), layout, support).dynamicInvoker();
    var guard = guardWithTest(test, mh, fallback);
    setTarget(guard);
    return mh;
  }

  enum Accessor { GET, SET }

  private static int getInt(long sizeof, long offset, MemorySegment segment, long index) {
    return segment.get(ValueLayout.JAVA_INT, index * sizeof + offset);
  }
  private static void setInt(long sizeof, long offset, MemorySegment segment, long index, int value) {
    segment.set(ValueLayout.JAVA_INT, index * sizeof + offset, value);
  }

  private static final MethodHandle GET_INT, SET_INT;

  static {
    var lookup = MethodHandles.lookup();
    try {
      GET_INT = lookup.findStatic(InlineCache.class, "getInt",
          methodType(int.class, long.class, long.class, MemorySegment.class, long.class));
      SET_INT = lookup.findStatic(InlineCache.class, "setInt",
          methodType(void.class, long.class, long.class, MemorySegment.class, long.class, int.class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  private static final Map<Accessor, Map<Class<?>, MethodHandle>> SUPPORT_MAP =
      Map.of(
          Accessor.GET, Map.of(int.class, GET_INT),
          Accessor.SET, Map.of(int.class, SET_INT)
      );

  static MethodHandle createMH(MemoryLayout layout, Class<?> type, Accessor accessor) {
    var support = SUPPORT_MAP.get(accessor).get(type);
    if (support == null) {
      throw new UnsupportedOperationException("not yet implemented , only ints are supported");
    }
    return new InlineCache(switch (accessor) {
      case GET -> methodType(type, MemorySegment.class, String.class, long.class);
      case SET -> methodType(void.class, MemorySegment.class, String.class, long.class, type);
    }, layout, support).dynamicInvoker();
  }
}