import com.github.josepaumard.stablevalue.StableValue;

private static final Supplier<MemoryLayout> DATA_LAYOUT = StableValue.supplier(() -> MemoryLayout.structLayout(
    ValueLayout.JAVA_BYTE.withName("kind"),
    MemoryLayout.paddingLayout(3),
    ValueLayout.JAVA_INT.withName("payload"),
    ValueLayout.JAVA_BYTE.withName("extra"),
    MemoryLayout.paddingLayout(3)
  ));

private static final Map<String, VarHandle> MAP_VH =
    StableValue.map(Set.of("kind", "payload", "extra"), name -> {
      return DATA_LAYOUT.get()
          .varHandle(MemoryLayout.PathElement.groupElement(name))
          .withInvokeExactBehavior();
    });

void main() {
  IO.println("layout " + DATA_LAYOUT.get());  // [b1(kind)x3i4(payload)b1(extra)x3]

  try(var arena = Arena.ofConfined()) {
    var data = arena.allocate(DATA_LAYOUT.get());

    MAP_VH.get("kind").set(data, 0L, (byte) 12);
    IO.println((byte) MAP_VH.get("kind").get(data, 0L));
  }
}
