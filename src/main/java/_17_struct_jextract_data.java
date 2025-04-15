import com.github.josepaumard.jextractdemo.data;

void main() {
  IO.println("layout " + data.layout());  // [b1(kind)x3i4(payload)b1(extra)x3](data)

  IO.println("sizeof " + data.sizeof());  // 12
}
