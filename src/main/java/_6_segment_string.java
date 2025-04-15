import static java.nio.charset.StandardCharsets.UTF_8;

void main() {
  var segment = Arena.global().allocate(512L);
  IO.println("segment " + segment);

  segment.setString(0, "hello", UTF_8);  // adds a '\0' at the end
  var text = segment.getString(0, UTF_8);
  IO.println(text);

  // String API is C oriented (ends with '\0')
}
