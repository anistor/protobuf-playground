package com.example.encoding;

/**
 * @author anistor@redhat.com
 */
public interface MessageFactory {

   <T extends Message> T createInstance(Class<T> messageInterface);
}
