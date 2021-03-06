package com.example.domain_with_converters.domain;

import java.util.List;

/**
 * @author anistor@redhat.com
 */
public class User {

   public enum Gender {
      MALE, FEMALE
   }

   private int id;
   private String name;
   private String surname;
   private List<Integer> accountIds;
   private List<Address> addresses;
   private Integer age;
   private Gender gender;

   //todo [anistor] were do we store the unknown fields in this case?

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getSurname() {
      return surname;
   }

   public void setSurname(String surname) {
      this.surname = surname;
   }

   public List<Integer> getAccountIds() {
      return accountIds;
   }

   public void setAccountIds(List<Integer> accountIds) {
      this.accountIds = accountIds;
   }

   public List<Address> getAddresses() {
      return addresses;
   }

   public void setAddresses(List<Address> addresses) {
      this.addresses = addresses;
   }

   public Integer getAge() {
      return age;
   }

   public void setAge(Integer age) {
      this.age = age;
   }

   public Gender getGender() {
      return gender;
   }

   public void setGender(Gender gender) {
      this.gender = gender;
   }

   @Override
   public String toString() {
      return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", surname='" + surname + '\'' +
            ", accountIds=" + accountIds +
            ", addresses=" + addresses +
            ", age=" + age +
            ", gender=" + gender +
            '}';
   }
}
