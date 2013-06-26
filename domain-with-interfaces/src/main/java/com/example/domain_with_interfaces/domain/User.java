package com.example.domain_with_interfaces.domain;

import com.example.encoding.BaseMessage;
import com.example.generated_by_codegen.Bank;
import com.google.protobuf.UnknownFieldSet;

import java.util.List;

/**
 * @author anistor@redhat.com
 */
public class User extends BaseMessage implements Bank.User {

   private int id;
   private String name;
   private String surname;
   private List<Integer> accountId;
   private List<Bank.User.Address> address;
   private Integer age;
   private Gender gender;
   private UnknownFieldSet unknownFieldSet;

   @Override
   public int getId() {
      return id;
   }

   @Override
   public void setId(int id) {
      this.id = id;
   }

   @Override
   public List<Integer> getAccountId() {
      return accountId;
   }

   @Override
   public void setAccountId(List<Integer> accountId) {
      this.accountId = accountId;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String getSurname() {
      return surname;
   }

   @Override
   public void setSurname(String surname) {
      this.surname = surname;
   }

   @Override
   public List<Bank.User.Address> getAddress() {
      return address;
   }

   @Override
   public void setAddress(List<Bank.User.Address> address) {
      this.address = address;
   }

   @Override
   public Integer getAge() {
      return age;
   }

   @Override
   public void setAge(Integer age) {
      this.age = age;
   }

   @Override
   public Gender getGender() {
      return gender;
   }

   @Override
   public void setGender(Gender gender) {
      this.gender = gender;
   }

   @Override
   public String toString() {
      return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", surname='" + surname + '\'' +
            ", accountId=" + accountId +
            ", address=" + address +
            ", age=" + age +
            ", gender=" + gender +
            ", unknownFieldSet=" + unknownFieldSet +
            '}';
   }
}
