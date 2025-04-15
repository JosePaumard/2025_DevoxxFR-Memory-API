/* On macOs : sudo xattr -r -d com.apple.quarantine /Users/forax/jdk/jextract-22/
   /Users/forax/jdk/jextract-22/bin/jextract \
     --output src/main/java \
     --target-package com.github.josepaumard.jextractdemo \
     src/main/c/library.h
*/

struct point {
  int x;
  int y;
};

struct data {
  char kind;
  int payload;
  char extra;
};

