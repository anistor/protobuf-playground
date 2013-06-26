package com.example.domain_with_interfaces.domain;

import com.example.encoding.BaseMessage;
import com.example.generated_by_codegen.Bank;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author anistor@redhat.com
 */
public class Transaction extends BaseMessage implements Bank.Transaction {

   private int id;
   private String description;
   private int accountId;
   private Date date;
   private BigDecimal amount;
   private boolean isDebit;

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
   public int getAccountId() {
      return accountId;
   }

   @Override
   public void setAccountId(int accountId) {
      this.accountId = accountId;
   }

   @Override
   public long getDate() {
      return date.getTime();
   }

   public void setDate(Date date) {
      this.date = date;
   }

   @Override
   public void setDate(long date) {
      this.date = new Date(date);
   }

   @Override
   public double getAmount() {
      return amount.doubleValue();
   }

   @Override
   public void setAmount(double amount) {
      this.amount = new BigDecimal(amount);
   }

   public void setAmount(BigDecimal amount) {
      this.amount = amount;
   }

   @Override
   public boolean getDebit() {
      return isDebit;
   }

   @Override
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
            ", unknownFieldSet=" + unknownFieldSet +
            '}';
   }
}
