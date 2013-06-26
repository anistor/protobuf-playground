package com.example.encoding;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

/**
 * @author anistor@redhat.com
 */
public interface Encoder<T extends Message> {

   int computeSize(T o);

   void encode(CodedOutputStream out, T o) throws java.io.IOException;

   T decode(CodedInputStream in, MessageFactory mf) throws java.io.IOException;
}
