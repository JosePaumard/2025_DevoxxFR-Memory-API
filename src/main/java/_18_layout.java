private static final MemoryLayout DATA_LAYOUT = MemoryLayout.structLayout(
    ValueLayout.JAVA_BYTE.withName("kind"),
    MemoryLayout.paddingLayout(3),   // <-- comment this line
    ValueLayout.JAVA_INT.withName("payload"),
    ValueLayout.JAVA_BYTE.withName("extra"),
    MemoryLayout.paddingLayout(3)
);

void main() {
  IO.println("layout " + DATA_LAYOUT);            // [b1(kind)x3i4(payload)b1(extra)x3]
  // IO.println("layout " + DATA_LAYOUT_UNALIGNED);  // [b1(kind)1%i4(payload)b1(extra)]
}

private static final MemoryLayout DATA_LAYOUT_UNALIGNED = MemoryLayout.structLayout(
    ValueLayout.JAVA_BYTE.withName("kind"),
    ValueLayout.JAVA_INT_UNALIGNED.withName("payload"),
    ValueLayout.JAVA_BYTE.withName("extra")
);
