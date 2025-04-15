private static final MemoryLayout DATA_LAYOUT = MemoryLayout.structLayout(
    ValueLayout.JAVA_BYTE.withName("kind"),
    MemoryLayout.paddingLayout(3),
    ValueLayout.JAVA_INT.withName("payload"),
    ValueLayout.JAVA_BYTE.withName("extra"),
    MemoryLayout.paddingLayout(3)
);

private static final VarHandle KIND_VH =
    DATA_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("kind"));

void main() {
  IO.println("layout " + DATA_LAYOUT);  // [b1(kind)x3i4(payload)b1(extra)x3]

  try(var arena = Arena.ofConfined()) {
    var data = arena.allocate(DATA_LAYOUT);

    // KIND_VH.set(data, 0L, 12);   // WMTE

    KIND_VH.set(data, 0L, (byte) 12);
    IO.println(KIND_VH.get(data, 0L));   // There is a perf bug here !!
  }
}
