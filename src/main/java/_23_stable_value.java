import com.github.josepaumard.stablevalue.StableValue;

private static final Supplier<MemoryLayout> DATA_LAYOUT = StableValue.supplier(() -> MemoryLayout.structLayout(
    ValueLayout.JAVA_BYTE.withName("kind"),
    MemoryLayout.paddingLayout(3),
    ValueLayout.JAVA_INT.withName("payload"),
    ValueLayout.JAVA_BYTE.withName("extra"),
    MemoryLayout.paddingLayout(3)
  ));

private static final Supplier<VarHandle> KIND_VH =
    StableValue.supplier(() -> {
      return DATA_LAYOUT.get()
          .varHandle(MemoryLayout.PathElement.groupElement("kind"))
          .withInvokeExactBehavior();
    });

void main() {
  IO.println("layout " + DATA_LAYOUT.get());  // [b1(kind)x3i4(payload)b1(extra)x3]

  try(var arena = Arena.ofConfined()) {
    var data = arena.allocate(DATA_LAYOUT.get());

    KIND_VH.get().set(data, 0L, (byte) 12);
    IO.println((byte) KIND_VH.get().get(data, 0L));
  }
}
