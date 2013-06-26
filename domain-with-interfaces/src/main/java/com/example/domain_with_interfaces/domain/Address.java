package com.example.domain_with_interfaces.domain;

import com.example.encoding.BaseMessage;
import com.example.generated_by_codegen.Bank;

/**
 * @author anistor@redhat.com
 */
public class Address extends BaseMessage implements Bank.User.Address {

   private String street;
   private String postCode;

   public Address() {
   }

   public Address(String street, String postCode) {
      this.street = street;
      this.postCode = postCode;
   }

   @Override
   public String getStreet() {
      return street;
   }

   @Override
   public void setStreet(String street) {
      this.street = street;
   }

   @Override
   public String getPostCode() {
      return postCode;
   }

   @Override
   public void setPostCode(String postCode) {
      this.postCode = postCode;
   }

   @Override
   public String toString() {
      return "Address{" +
            "street='" + street + '\'' +
            ", postCode='" + postCode + '\'' +
            ", unknownFieldSet='" + unknownFieldSet + '\'' +
            '}';
   }
}
