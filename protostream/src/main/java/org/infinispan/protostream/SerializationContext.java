package org.infinispan.protostream;

// todo
// https://gist.github.com/mmarkus/5782470
// https://gist.github.com/anistor/e32ead4c84a9d2ae43d0
// https://gist.github.com/anistor/dca309b411cbf2f9579d

import com.google.protobuf.Descriptors;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author anistor@redhat.com
 */
public interface SerializationContext {

   /**
    * The input stream is not closed when finished.
    *
    * @param in
    */
   void registerProtofile(InputStream in) throws IOException, Descriptors.DescriptorValidationException;

   void registerProtofile(String classpathResource) throws IOException, Descriptors.DescriptorValidationException;

   void registerProtofile(Descriptors.FileDescriptor fileDescriptor);

   Descriptors.Descriptor getMessageDescriptor(String fullName);

   Descriptors.EnumDescriptor getEnumDescriptor(String fullName);

   <T> void registerMarshaller(Class<T> clazz, MessageMarshaller<T> marshaller);

   /**
    * Checks if the message or enum type can be marshalled (a marshaller is defined for it).
    *
    * @param clazz
    * @return
    */
   boolean canMarshall(Class clazz);

   <T> MessageMarshaller<T> getMarshaller(String descriptorFullName);

   <T> MessageMarshaller<T> getMarshaller(Class<T> clazz);

   <T extends Enum<T>> void registerEnumEncoder(Class<T> clazz, EnumEncoder<T> enumEncoder);

   <T extends Enum<T>> EnumEncoder<T> getEnumEncoder(String descriptorFullName);

   <T extends Enum<T>> EnumEncoder<T> getEnumEncoder(Class<T> clazz);
}
