package miw.s16.couch.couch.model.entity;


import miw.s16.couch.couch.model.RetailUser;
import miw.s16.couch.couch.model.Transaction;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import javax.persistence.*;


import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

//coding by PvdH

@Entity

public class BankAccount implements Serializable {

@Id
@GeneratedValue
    private int bankAccountId;
    private String iBAN;
    private double balance;

//    @OneToMany
//    private List<Transaction> transactions;
//    @ManyToMany
//    private List<RetailUser> retailusers;

    //constructors
    public BankAccount () {
        this(0);
      }

    public BankAccount(double balance){
        this.iBAN = generateIban();
        this.bankAccountId = 0;
        this.balance = balance;
    }

    public BankAccount(String iBAN, double balance){
        this.iBAN = iBAN;
        this.balance = balance;
        this.bankAccountId = 0;
    }

      public BankAccount(int bankAccountId, String iBAN, double balance) {
        super();
        this.bankAccountId = bankAccountId;
        this.iBAN = iBAN;
        this.balance = balance;
//        this.transactions = new ArrayList<>();
//        this.retailusers = new ArrayList<>();
    }

    //getters
    public String getIBAN() { return iBAN; }
    public double getBalance() {return balance;}
//    public void addTransactions (Transaction transaction) {transactions.add(transaction);}
//    public void addRetailUser (RetailUser retailuser) {retailusers.add(retailuser);}

    //setter for changes in balance, transactions and retailusers
    public void setBalance(double balance) { this.balance = balance; }

/*
    IBAN rules: https://nl.wikipedia.org/wiki/International_Bank_Account_Number
    If an account number starts with a 1 it's an internal account, if it starts with a 0 its a customer (SME or Retail) account
    The other starting numbers are never used
*/

    public long generateAccount() {
        final long MAX_ACC_NR = 999999999L;
        final long MIN_ACC_NR = 0L;
        return (long) ((MAX_ACC_NR - MIN_ACC_NR + 1) * Math.random()) + MIN_ACC_NR;       //generate 9 digit acc nr; first digit is always 0
    }

    public int generateCheckDigits(long account) {
        account *= 1000000L;                               //add 'NL' numerical and 00 to end of acc nr according to IBAN rules
        account += 232100L;
        BigInteger bigaccount;                              //convert to BigInteger because long is too short
        bigaccount = BigInteger.valueOf(account);
        bigaccount = bigaccount.add(new BigInteger("122430120000000000000000"));     //add 'COUC' numerical according to IBAN rules
        return (new BigInteger("98").subtract(bigaccount.mod(new BigInteger("97")))).intValue();        //calculate & return check digits
    }

    public String generateAccountAs10digitString(long account){
        String accAsString = Long.toString(account);
        StringBuilder tenDigitAccount = new StringBuilder(accAsString);
        for (int i = 0; i < 10-accAsString.length(); i++){
            tenDigitAccount.insert(0, 0);                   //add zero's in front of account number until it's a 10 digit number
        }
        return tenDigitAccount.toString();
    }

    /*public boolean checkIfIbanAlreadyExists(long account){
        String accountString =  Long.toString(account);
        List<BankAccount> l = bankAccountDao.findDistinctByIBANEndsWith(accountString);     //check if generated account nr aleardy exists
        if (l.size() == 0){
            return false;
        } else {
            return true;
        }
    }

     */

    public String generateIban() {
        long account = generateAccount();                                 //when database is up, check if IBAN already
        /*boolean duplicateCheck = checkIfIbanAlreadyExists(account);
            if (duplicateCheck){
                return generateIban();                                      //dangerous code: endless loop if (close to) all possible IBANs are generated
            } else {*/
                int checkDigits = generateCheckDigits(account);
                StringBuilder iban = new StringBuilder();
                iban.append("NL");
                if (checkDigits < 10) {                                              //if checkdigits < 10, add a 0 in front
                    String checkDigitsString = Integer.toString(checkDigits);
                    checkDigitsString = "0" + checkDigitsString;
                    iban.append(checkDigitsString);
                } else {
                    iban.append(checkDigits);
                }
                iban.append("COUC");
                iban.append(generateAccountAs10digitString(account));
                return iban.toString();
            }
    //}




    @Override
    public String toString() {
        return iBAN;
    }

}
