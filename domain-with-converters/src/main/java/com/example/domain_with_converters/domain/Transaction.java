package com.example.domain_with_converters.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author anistor@redhat.com
 */
public class Transaction {

   private int id;
   private String description;
   private int accountId;
   private Date date;
   private BigDecimal amount;
   private boolean isDebit;

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

   public int getAccountId() {
      return accountId;
   }

   public void setAccountId(int accountId) {
      this.accountId = accountId;
   }

   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public BigDecimal getAmount() {
      return amount;
   }

   public void setAmount(BigDecimal amount) {
      this.amount = amount;
   }

   public boolean isDebit() {
      return isDebit;
   }

   public void setDebit(boolean debit) {
      this.isDebit = debit;
   }

   @Override
   public String toString() {
      return "Transaction{" +
            "id=" + id +
            ", description='" + description + '\'' +
            ", accountId=" + accountId +
            ", date=" + date +
            ", amount=" + amount +
            ", isDebit=" + isDebit +
            '}';
   }
}
