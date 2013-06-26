package com.example.domain_with_converters.encoding;

import com.example.domain_with_converters.domain.Account;
import com.example.domain_with_converters.domain.Address;
import com.example.domain_with_converters.domain.Transaction;
import com.example.domain_with_converters.domain.User;
import com.example.generated_by_protoc.BankProtos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Transform domain entities to wire bytes and back again via google's protoc generated parser/builder objects.
 *
 * @author anistor@redhat.com
 */
public class Encoder {

   public byte[] encodeUser(User user) throws IOException {
      BankProtos.User.Builder userBuilder = BankProtos.User.newBuilder();
      userBuilder.setId(user.getId());
      userBuilder.setName(user.getName());
      userBuilder.setSurname(user.getSurname());

      if (user.getAccountIds() != null) {
         for (Integer accountId : user.getAccountIds()) {
            userBuilder.addAccountId(accountId);
         }
      }

      if (user.getAddresses() != null) {
         for (Address a : user.getAddresses()) {
            BankProtos.User.Address.Builder addressBuilder = BankProtos.User.Address.newBuilder();
            addressBuilder.setStreet(a.getStreet());
            addressBuilder.setPostCode(a.getPostCode());
            userBuilder.addAddress(addressBuilder);
         }
      }

      if (user.getAge() != null) {
         userBuilder.setAge(user.getAge());
      }

      if (user.getGender() != null) {
         BankProtos.User.Gender gender;
         if (user.getGender() == User.Gender.FEMALE) {
            gender = BankProtos.User.Gender.FEMALE;
         } else {
            gender = BankProtos.User.Gender.MALE;
         }
         userBuilder.setGender(gender);
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      userBuilder.build().writeTo(baos);
      return baos.toByteArray();
   }

   public User decodeUser(byte[] bytes) throws IOException {
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      BankProtos.User wireUser = BankProtos.User.parseFrom(bais);

      User user = new User();
      user.setId(wireUser.getId());
      user.setName(wireUser.getName());
      user.setSurname(wireUser.getSurname());

      if (wireUser.getAccountIdCount() > 0) {
         user.setAccountIds(wireUser.getAccountIdList());
      }

      if (wireUser.getAddressCount() > 0) {
         List<Address> addresses = new ArrayList<Address>(wireUser.getAddressCount());
         for (int i = 0; i < wireUser.getAddressCount(); i++) {
            BankProtos.User.Address a = wireUser.getAddress(i);
            Address address = new Address();
            address.setStreet(a.getStreet());
            address.setPostCode(a.getPostCode());
            addresses.add(address);
         }
         user.setAddresses(addresses);
      }

      if (wireUser.hasAge()) {
         user.setAge(wireUser.getAge());
      }

      if (wireUser.hasGender()) {
         User.Gender gender;
         if (wireUser.getGender() == BankProtos.User.Gender.FEMALE) {
            gender = User.Gender.FEMALE;
         } else {
            gender = User.Gender.MALE;
         }
         user.setGender(gender);
      }

      return user;
   }

   public byte[] encodeAccount(Account account) throws IOException {
      BankProtos.Account.Builder accountBuilder = BankProtos.Account.newBuilder();
      accountBuilder.setId(account.getId());
      accountBuilder.setDescription(account.getDescription());

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      accountBuilder.build().writeTo(baos);
      return baos.toByteArray();
   }

   public Account decodeAccount(byte[] bytes) throws IOException {
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      BankProtos.Account wireAccount = BankProtos.Account.parseFrom(bais);

      Account account = new Account();
      account.setId(wireAccount.getId());
      account.setDescription(wireAccount.getDescription());
      return account;
   }

   public byte[] encodeTransaction(Transaction transaction) throws IOException {
      BankProtos.Transaction.Builder transactionBuilder = BankProtos.Transaction.newBuilder();
      transactionBuilder.setId(transaction.getId());
      transactionBuilder.setDescription(transaction.getDescription());
      transactionBuilder.setAccountId(transaction.getAccountId());
      transactionBuilder.setDate(transaction.getDate().getTime());
      transactionBuilder.setAmount(transaction.getAmount().doubleValue()); // oops, need better way to represent a BigDecimal in order to preserve the exact number
      transactionBuilder.setDebit(transaction.isDebit());

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      transactionBuilder.build().writeTo(baos);
      return baos.toByteArray();
   }

   public Transaction decodeTransaction(byte[] bytes) throws IOException {
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      BankProtos.Transaction wireTransaction = BankProtos.Transaction.parseFrom(bais);

      Transaction transaction = new Transaction();
      transaction.setId(wireTransaction.getId());
      transaction.setDescription(wireTransaction.getDescription());
      transaction.setAccountId(wireTransaction.getAccountId());
      transaction.setDate(new Date(wireTransaction.getDate()));
      transaction.setAmount(new BigDecimal(wireTransaction.getAmount()));
      transaction.setDebit(wireTransaction.getDebit());
      return transaction;
   }
}
