import com.github.josepaumard.access.Access;

private static final MemoryLayout DATA_LAYOUT = MemoryLayout.structLayout(
    ValueLayout.JAVA_BYTE.withName("kind"),
    MemoryLayout.paddingLayout(3),
    ValueLayout.JAVA_INT.withName("payload"),
    ValueLayout.JAVA_BYTE.withName("extra"),
    MemoryLayout.paddingLayout(3)
);

private static final Access ACCESS = Access.of(DATA_LAYOUT);

void main() {
  IO.println("layout " + DATA_LAYOUT);  // [b1(kind)x3i4(payload)b1(extra)x3]

  try(var arena = Arena.ofConfined()) {
    var array = arena.allocate(DATA_LAYOUT, 64L);
    for(var i = 0; i < 64; i++) {
      ACCESS.setAtIndex(array, i, "payload", i);
    }

    for(var i = 0; i < 4; i++) {
      IO.println(ACCESS.getAtIndex(array, i,"payload"));
    }
  }
}
