private static final MemoryLayout DATA_LAYOUT = MemoryLayout.structLayout(
    ValueLayout.JAVA_BYTE.withName("kind"),
    MemoryLayout.paddingLayout(3),
    ValueLayout.JAVA_INT.withName("payload"),
    ValueLayout.JAVA_BYTE.withName("extra"),
    MemoryLayout.paddingLayout(3)
);

private static final long PAYLOAD_OFFSET =
    DATA_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("kind"));

private static final long DATA_SIZEOF =
    DATA_LAYOUT.byteSize();

void main() throws InterruptedException {
  IO.println("layout " + DATA_LAYOUT);  // [b1(kind)x3i4(payload)b1(extra)x3]

  try(var arena = Arena.ofConfined()) {
    var array = arena.allocate(DATA_LAYOUT, 64L);
    for(var i = 0; i < 64; i++) {
      var slice = array.asSlice(i * DATA_SIZEOF, DATA_LAYOUT);
      slice.set(ValueLayout.JAVA_INT, PAYLOAD_OFFSET, i);
    }

    for(var i = 0; i < 4; i++) {
      IO.println(array.asSlice(i * DATA_SIZEOF, DATA_LAYOUT)
          .get(ValueLayout.JAVA_INT, PAYLOAD_OFFSET));
    }
  }
}
