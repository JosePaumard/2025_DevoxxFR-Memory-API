package com.github.josepaumard.mapper;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles.Lookup;

import static java.util.Objects.requireNonNull;

public sealed interface RecordMapper<T extends Record> permits RecordMapperImpl {
  T get(MemorySegment segment);
  void set(MemorySegment segment, T value);

  T getAtIndex(MemorySegment segment, long index);
  void setAtIndex(MemorySegment segment, long index, T record);

  static <T extends Record> RecordMapper<T> of(Lookup lookup, MemoryLayout layout, Class<T> recordClass) {
    requireNonNull(layout, "layout is null");
    requireNonNull(recordClass, "recordClass is null");
    var components = recordClass.getRecordComponents();
    if (components == null) {
      throw new IllegalArgumentException(recordClass.getName() + " is not a record class");
    }
    var getter = RecordMapperImpl.getter(lookup, layout, recordClass, components);
    var setter = RecordMapperImpl.setter(lookup, layout, recordClass, components);
    return new RecordMapperImpl<>(layout.byteSize(), getter, setter);
  }
}
