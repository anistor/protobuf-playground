package com.example.domain_with_interfaces.domain;

import com.example.encoding.BaseMessage;
import com.example.generated_by_codegen.Bank;

/**
 * @author anistor@redhat.com
 */
public class Account extends BaseMessage implements Bank.Account {

   private int id;
   private String description;
   private Limits limits;

   @Override
   public int getId() {
      return id;
   }

   @Override
   public void setId(int id) {
      this.id = id;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public void setDescription(String description) {
      this.description = description;
   }

   @Override
   public Limits getLimits() {
      return limits;
   }

   @Override
   public void setLimits(Limits limits) {
      this.limits = limits;
   }

   @Override
   public String toString() {
      return "Account{" +
            "id=" + id +
            ", description='" + description + '\'' +
            ", limits='" + limits + '\'' +
            ", unknownFieldSet='" + unknownFieldSet + '\'' +
            '}';
   }
}
