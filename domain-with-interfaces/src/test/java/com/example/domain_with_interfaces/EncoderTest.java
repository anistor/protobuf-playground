package com.example.domain_with_interfaces;

import com.example.domain_with_interfaces.domain.Account;
import com.example.domain_with_interfaces.domain.Address;
import com.example.domain_with_interfaces.domain.Limits;
import com.example.domain_with_interfaces.domain.Transaction;
import com.example.domain_with_interfaces.domain.User;
import com.example.encoding.DefaultMessageFactory;
import com.example.generated_by_codegen.Bank;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author anistor@redhat.com
 */
public class EncoderTest {

   @Test
   public void test() throws Exception {
      DefaultMessageFactory messageFactory = new DefaultMessageFactory();
      messageFactory.registerImplementation(Bank.User.class, User.class);
      messageFactory.registerImplementation(Bank.User.Address.class, Address.class);
      messageFactory.registerImplementation(Bank.Transaction.class, Transaction.class);
      messageFactory.registerImplementation(Bank.Account.class, Account.class);
      messageFactory.registerImplementation(Bank.Account.Limits.class, Limits.class);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      CodedOutputStream out = CodedOutputStream.newInstance(baos);

      User user = new User();
      user.setId(1);
      user.setName("John");
      user.setSurname("Batman");

      user.setGender(User.Gender.MALE);
      Bank.User.Address address = new Address("Old Street", "XYZ42");
      user.setAddress(Collections.singletonList(address));

      user.setAccountId(Arrays.asList(1, 3));

      Bank.User.ENCODER.encode(out, user);

      out.flush();
      baos.close();

      byte[] bytes = baos.toByteArray();
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      CodedInputStream in = CodedInputStream.newInstance(bais);

      Bank.User decoded = Bank.User.ENCODER.decode(in, messageFactory);  // hmm ... it can't return my domain type, it returns the interface ...

      assertEquals(1, decoded.getId());
      assertEquals("John", decoded.getName());
      assertEquals("Batman", decoded.getSurname());
      assertEquals(User.Gender.MALE, decoded.getGender());

      assertNotNull(decoded.getAddress());
      assertEquals(1, decoded.getAddress().size());
      assertEquals("Old Street", decoded.getAddress().get(0).getStreet());
      assertEquals("XYZ42", decoded.getAddress().get(0).getPostCode());

      assertNotNull(decoded.getAccountId());
      assertEquals(2, decoded.getAccountId().size());
      assertEquals(1, decoded.getAccountId().get(0).intValue());
      assertEquals(3, decoded.getAccountId().get(1).intValue());
      // successful teleportation, no DNA damage. Batman is alive!
   }
}
