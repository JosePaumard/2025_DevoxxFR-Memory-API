import com.github.josepaumard.mapper.RecordMapper;

private static final MemoryLayout DATA_LAYOUT = MemoryLayout.structLayout(
    ValueLayout.JAVA_BYTE.withName("kind"),
    MemoryLayout.paddingLayout(3),
    ValueLayout.JAVA_INT.withName("payload"),
    ValueLayout.JAVA_BYTE.withName("extra"),
    MemoryLayout.paddingLayout(3)
);

private /*value*/ record Data(int payload) {}
private static final RecordMapper<Data> MAPPER =
      RecordMapper.of(MethodHandles.lookup(), DATA_LAYOUT, Data.class);

void main() {
  IO.println("layout " + DATA_LAYOUT);  // [b1(kind)x3i4(payload)b1(extra)x3]

  try(var arena = Arena.ofConfined()) {
    var array = arena.allocate(DATA_LAYOUT, 64L);
    for(var i = 0; i < 64; i++) {
      MAPPER.setAtIndex(array, i, new Data(i));
    }

    for(var i = 0; i < 4; i++) {
        Data dataAtIndex = MAPPER.getAtIndex(array, i);
        IO.println(dataAtIndex.payload);
    }
  }
}
