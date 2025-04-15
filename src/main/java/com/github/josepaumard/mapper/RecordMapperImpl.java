package com.github.josepaumard.mapper;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;
import static java.lang.invoke.MethodHandles.Lookup;
import static java.lang.invoke.MethodHandles.filterArguments;
import static java.lang.invoke.MethodHandles.insertArguments;
import static java.lang.invoke.MethodHandles.permuteArguments;
import static java.lang.invoke.MethodType.methodType;
import static java.util.Objects.requireNonNull;

record RecordMapperImpl<T extends Record>(long sizeof, MethodHandle getter, MethodHandle setter) implements RecordMapper<T> {
    @Override
    @SuppressWarnings("unchecked")
    public T get(MemorySegment segment) {
      requireNonNull(segment, "segment is null");
      try {
        return (T) getter.invokeExact(segment);
      } catch (Throwable e) {
        throw rethrow(e);
      }
    }

    @Override
    public void set(MemorySegment segment, T record) {
      requireNonNull(segment, "segment is null");
      requireNonNull(record, "record is null");
      try {
        setter.invokeExact(segment, record);
      } catch (Throwable e) {
        throw rethrow(e);
      }
    }

  @Override
  public T getAtIndex(MemorySegment segment, long index) {
    return get(segment.asSlice(index * sizeof, sizeof));
  }

  @Override
  public void setAtIndex(MemorySegment segment, long index, T record) {
    set(segment.asSlice(index * sizeof, sizeof), record);
  }

  @SuppressWarnings("unchecked")
    private static <X extends Throwable> RuntimeException rethrow(Throwable throwable) throws X {
      throw (X) throwable;
    }

    private static final MethodHandle GET_INT, SET_INT;
    static {
      var lookup = MethodHandles.lookup();
      try {
        GET_INT = lookup.findStatic(RecordMapperImpl.class, "getInt",
            methodType(int.class, MemorySegment.class, long.class));
        SET_INT = lookup.findStatic(RecordMapperImpl.class, "setInt",
            methodType(void.class, MemorySegment.class, long.class, int.class));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }

    private static void setInt(MemorySegment memorySegment, long offset, int value) {
      memorySegment.set(ValueLayout.JAVA_INT, offset, value);
    }
    private static int getInt(MemorySegment memorySegment, long offset) {
      return memorySegment.get(ValueLayout.JAVA_INT, offset);
    }

    static MethodHandle setter(Lookup lookup, MemoryLayout layout, Class<?> recordClass, RecordComponent[] components) {
      var setter = MethodHandles.empty(methodType(void.class, MemorySegment.class, recordClass));
      for (var component : components) {
        MethodHandle accessor;
        try {
          accessor = lookup.unreflect(component.getAccessor());
        } catch (IllegalAccessException e) {
          throw (IllegalAccessError) new IllegalAccessError().initCause(e);
        }
        var name = component.getName();
        var type = component.getType();
        if (!type.isPrimitive()) {
          throw new IllegalStateException("component " + name + " with type " + type.getName() + " is not a primitive");
        }
        var support = switch (type.getName()) {
          case "int" -> SET_INT;
          default -> throw new UnsupportedOperationException("not yet implemented , only ints are supported");
        };
        var mh = filterArguments(support, 2, accessor);
        mh = insertArguments(mh, 1, layout.byteOffset(groupElement(name)));
        setter = MethodHandles.foldArguments(setter, mh);
      }
      return setter.asType(methodType(void.class, MemorySegment.class, Record.class));
    }

    static MethodHandle getter(Lookup lookup, MemoryLayout layout, Class<?> recordClass, RecordComponent[] components) {
      var parameterTypes = Arrays.stream(components).map(RecordComponent::getType).toArray(Class<?>[]::new);
      MethodHandle constructor;
      try {
        constructor = lookup.findConstructor(recordClass, methodType(void.class, parameterTypes));
      } catch (NoSuchMethodException e) {
        throw (NoSuchMethodError) new NoSuchMethodError().initCause(e);
      } catch (IllegalAccessException e) {
        throw (IllegalAccessError) new IllegalAccessError().initCause(e);
      }
      var filters = Arrays.stream(components)
          .map(component -> {
            var name = component.getName();
            var type = component.getType();
            if (!type.isPrimitive()) {
              throw new IllegalStateException("component " + name + " with type " + type.getName() + " is not a primitive");
            }
            var support = switch (type.getName()) {
              case "int" -> GET_INT;
              default -> throw new UnsupportedOperationException("not yet implemented , only ints are supported");
            };
            return insertArguments(support, 1, layout.byteOffset(groupElement(name)));
          })
          .toArray(MethodHandle[]::new);
      var filtered = filterArguments(constructor, 0, filters);
      var getter = permuteArguments(filtered, methodType(recordClass, MemorySegment.class), new int[filters.length]);
      return getter.asType(methodType(Record.class, MemorySegment.class));
    }
  }