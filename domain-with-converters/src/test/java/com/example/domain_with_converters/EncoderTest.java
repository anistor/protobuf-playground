package com.example.domain_with_converters;

import com.example.domain_with_converters.domain.Address;
import com.example.domain_with_converters.domain.User;
import com.example.domain_with_converters.encoding.Encoder;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Demonstrates usage of the Encoder class.
 *
 * @author anistor@redhat.com
 */
public class EncoderTest {

   @Test
   public void test() throws Exception {
      User user = new User();
      user.setId(1);
      user.setName("John");
      user.setSurname("Batman");
      user.setGender(User.Gender.MALE);
      user.setAddresses(Collections.singletonList(new Address("Old Street", "XYZ42")));
      user.setAccountIds(Arrays.asList(1, 3));

      Encoder encoder = new Encoder();
      byte[] bytes = encoder.encodeUser(user);
      User decoded = encoder.decodeUser(bytes);

      assertEquals(1, decoded.getId());
      assertEquals("John", decoded.getName());
      assertEquals("Batman", decoded.getSurname());
      assertEquals(User.Gender.MALE, decoded.getGender());

      assertNotNull(decoded.getAddresses());
      assertEquals(1, decoded.getAddresses().size());
      assertEquals("Old Street", decoded.getAddresses().get(0).getStreet());
      assertEquals("XYZ42", decoded.getAddresses().get(0).getPostCode());

      assertNotNull(decoded.getAccountIds());
      assertEquals(2, decoded.getAccountIds().size());
      assertEquals(1, decoded.getAccountIds().get(0).intValue());
      assertEquals(3, decoded.getAccountIds().get(1).intValue());
      // successful teleportation, no DNA damage. Batman is alive!
   }
}
