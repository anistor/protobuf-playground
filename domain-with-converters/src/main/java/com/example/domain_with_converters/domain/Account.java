package com.example.domain_with_converters.domain;

/**
 * @author anistor@redhat.com
 */
public class Account {

   private int id;
   private String description;

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   @Override
   public String toString() {
      return "Account{" +
            "id=" + id +
            ", description='" + description + '\'' +
            '}';
   }
}
