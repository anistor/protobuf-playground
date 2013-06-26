package com.example.encoding;

import com.google.protobuf.UnknownFieldSet;

/**
 * An evolvable message with support for preserving unknown fields.
 *
 * @author anistor@redhat.com
 */
public interface Message {

   UnknownFieldSet getUnknownFieldSet();

   void setUnknownFieldSet(UnknownFieldSet unknownFieldSet);
}
