package com.example.codegen;

import java.io.StringWriter;

/**
 * @author anistor@redhat.com
 */
public class IndentWriter extends StringWriter {

   private static final String TAB = "   ";
   private int indent = 0;
   private boolean indentNeeded = false;

   public void inc() {
      indent++;
   }

   public void dec() {
      if (indent > 0) {
         indent--;
      }
   }

   @Override
   public final void write(int c) {
      if (indentNeeded) {
         indentNeeded = false;
         for (int i = 0; i < indent; i++) {
            super.write(TAB);
         }
      }
      super.write(c);
      if (c == '\n') {
         indentNeeded = true;
      }
   }

   @Override
   public final void write(char[] cbuf, int off, int len) {
      for (int i = off; i < off + len; i++) {
         write(cbuf[i]);
      }
   }

   @Override
   public final void write(String str) {
      if (str == null) {
         str = "null";
      }
      write(str, 0, str.length());
   }

   @Override
   public final void write(String str, int off, int len) {
      for (int i = off; i < off + len; i++) {
         write(str.charAt(i));
      }
   }
}
