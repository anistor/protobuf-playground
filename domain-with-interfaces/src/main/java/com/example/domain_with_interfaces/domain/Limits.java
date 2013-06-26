package com.example.domain_with_interfaces.domain;

import com.example.encoding.BaseMessage;
import com.example.generated_by_codegen.Bank;

/**
 * @author anistor@redhat.com
 */
public class Limits extends BaseMessage implements Bank.Account.Limits {

   private Double maxDailyWithdrawalLimit;
   private Double maxDailyOnlineTransactionLimit;

   @Override
   public Double getMaxDailyWithdrawalLimit() {
      return maxDailyWithdrawalLimit;
   }

   @Override
   public void setMaxDailyWithdrawalLimit(Double maxDailyWithdrawalLimit) {
      this.maxDailyWithdrawalLimit = maxDailyWithdrawalLimit;
   }

   @Override
   public Double getMaxDailyOnlineTransactionLimit() {
      return maxDailyOnlineTransactionLimit;
   }

   @Override
   public void setMaxDailyOnlineTransactionLimit(Double maxDailyOnlineTransactionLimit) {
      this.maxDailyOnlineTransactionLimit = maxDailyOnlineTransactionLimit;
   }

   @Override
   public String toString() {
      return "Limits{" +
            "maxDailyWithdrawalLimit=" + maxDailyWithdrawalLimit +
            ", maxDailyOnlineTransactionLimit=" + maxDailyOnlineTransactionLimit +
            '}';
   }
}
