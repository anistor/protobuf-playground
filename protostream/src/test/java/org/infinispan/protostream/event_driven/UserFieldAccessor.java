package org.infinispan.protostream.event_driven;

import org.infinispan.protostream.domain.Address;
import org.infinispan.protostream.domain.User;

import java.util.ArrayList;

/**
 * @author anistor@redhat.com
 */
public class UserFieldAccessor implements MessageFieldAccessor<User> {

   private static final int NAME = 3;
   private static final int ADDRESS = 5;

   public Object createChild(User parentMessage, int fieldNumber, String fieldName) {
      if (fieldNumber == ADDRESS) {
         return new Address();
      }

      return null; // this means unknown field, should be added to UnknownFieldSet of parent if parent implements Message
   }

   public void setField(User user, int fieldNumber, String fieldName, Object value) {
      switch (fieldNumber) {
         case NAME: {
            user.setName((String) value);
         }
         break;

         case ADDRESS: {
            if (user.getAddresses() == null) {
               user.setAddresses(new ArrayList<Address>());
            }
            user.getAddresses().add((Address) value);
         }
         break;

         default:
            // if User implements Message interface, add the unknown field there, otherwise discard
      }
   }

   public Object getField(User user, int fieldNumber, String fieldName) {
      switch (fieldNumber) {
         case NAME:
            return user.getName();
         case ADDRESS:
            return user.getAddresses();
         default:
            // if User implements Message interface, try to get it from the UnknownFieldSet, otherwise just return null
      }
      return null;
   }
}
