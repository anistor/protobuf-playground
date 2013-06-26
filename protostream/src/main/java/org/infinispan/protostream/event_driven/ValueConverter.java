package org.infinispan.protostream.event_driven;

/**
 * @param <FROM>
 * @param <TO>
 * @author anistor@redhat.com
 */
public interface ValueConverter<FROM, TO> {

   TO forward(FROM x);

   FROM reverse(TO x);
}
