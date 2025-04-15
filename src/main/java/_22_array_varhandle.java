private static final MemoryLayout DATA_LAYOUT = MemoryLayout.structLayout(
    ValueLayout.JAVA_BYTE.withName("kind"),
    MemoryLayout.paddingLayout(3),
    ValueLayout.JAVA_INT.withName("payload"),
    ValueLayout.JAVA_BYTE.withName("extra"),
    MemoryLayout.paddingLayout(3)
);

private static final VarHandle ARRAY_PAYLOAD_VH =
    DATA_LAYOUT
        .arrayElementVarHandle(MemoryLayout.PathElement.groupElement("payload"))
        .withInvokeExactBehavior();

void main() {
  IO.println("layout " + DATA_LAYOUT);  // [b1(kind)x3i4(payload)b1(extra)x3]

  try(var arena = Arena.ofConfined()) {
    var array = arena.allocate(DATA_LAYOUT, 64L);
    for(var i = 0; i < 64; i++) {
      ARRAY_PAYLOAD_VH.set(array, 0L, (long) i, i + 10);
    }

    for(var i = 0; i < 4; i++) {
      IO.println((int) ARRAY_PAYLOAD_VH.get(array, 0L, (long) i));
    }
  }
}
