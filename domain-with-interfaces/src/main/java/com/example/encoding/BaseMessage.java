package com.example.encoding;

import com.google.protobuf.UnknownFieldSet;

/**
 * Base class for message implementations.
 *
 * @author anistor@redhat.com
 */
public class BaseMessage implements Message {

   protected UnknownFieldSet unknownFieldSet;

   @Override
   public UnknownFieldSet getUnknownFieldSet() {
      return unknownFieldSet;
   }

   @Override
   public void setUnknownFieldSet(UnknownFieldSet unknownFieldSet) {
      this.unknownFieldSet = unknownFieldSet;
   }
}
