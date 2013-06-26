package com.example.encoding;

import java.util.HashMap;
import java.util.Map;

/**
 * @author anistor@redhat.com
 */
public class DefaultMessageFactory implements MessageFactory {

   private Map<Class<? extends Message>, Class<? extends Message>> implementations = new HashMap<Class<? extends Message>, Class<? extends Message>>();

   @Override
   public <T extends Message> T createInstance(Class<T> messageInterface) {
      Class<? extends Message> implClass = implementations.get(messageInterface);
      if (implClass == null) {
         throw new RuntimeException("No implementation registered for " + messageInterface);
      }

      try {
         return (T) implClass.newInstance();
      } catch (RuntimeException e) {
         throw e;
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public <T extends Message> void registerImplementation(Class<T> messageInterface, Class<? extends T> messageImplementation) {
      implementations.put(messageInterface, messageImplementation);
   }
}
