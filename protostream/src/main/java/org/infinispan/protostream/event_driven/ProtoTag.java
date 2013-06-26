package org.infinispan.protostream.event_driven;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define the field number used to serialize this field to protobuf.
 *
 * @author anistor@redhat.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProtoTag {

   /**
    * The field number.
    */
   int value();

   /**
    * An optional converter if the value needs to be massaged.
    */
   Class<? extends ValueConverter> converter() default ValueConverter.class;
}
