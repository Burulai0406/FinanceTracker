package com.burulai;

public class Transaction {
    private String description;
   private double amount;
    private boolean isIncome;


   public Transaction(String description,double amount, boolean isIncome){
        this.description = description;
        this.isIncome = isIncome;
       setAmount(amount);
    }


    public  String getDescription(){
        return description;
    }
    public double getAmount(){
        return amount;
    }
    public boolean isIncome() {
        return isIncome;
    }

    public double getSignedAmount(){
        if(isIncome){
            return amount;
        }else{
            return -amount;
        }
    }


    public void setAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0!");
        }

        this.amount = amount;
    }


}
