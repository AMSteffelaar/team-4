package miw.s16.couch.couch.service;

import miw.s16.couch.couch.model.BankAccount;
import miw.s16.couch.couch.model.Loan;
import miw.s16.couch.couch.model.Transaction;
import miw.s16.couch.couch.model.dao.BankAccountDao;
import miw.s16.couch.couch.model.dao.TransactionDao;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLOutput;
import java.util.Date;

@Service
public class TransactionService {

    @Autowired
    TransactionDao transactionDao;

    @Autowired
    BankAccountDao bankAccountDao;


    public TransactionService() {
        super();

    }

    public String TransactionCalculation(String accountTo, BankAccount bankAccount, Double amount,
                                         Date transactionDate, String description, Boolean isPin) {

        BankAccount bankAccountTo = bankAccountDao.findByIban(accountTo);

        if (bankAccountTo != null) {
            double balance = bankAccountTo.getBalance();
            bankAccountTo.setBalance(balance + amount);
            double balanceFrom = bankAccount.getBalance();
            double newBalance = balanceFrom - amount;
            if (newBalance < 0) {
                return "Mislukt. Not enough money left in your account";
            } else {
                bankAccount.setBalance(balanceFrom - amount);
                // save changed in DB
                Transaction transaction = new Transaction(description, amount, transactionDate,
                        bankAccount, bankAccountTo, accountTo, bankAccount.getIBAN().toString(), isPin);

                bankAccountDao.save(bankAccountTo);
                bankAccountDao.save(bankAccount);
                // message for testing
                System.out.println("Voordat transaction is gevuld is transaction: " +
                        transaction);
                transactionDao.save(transaction);
                return "\nBedankt!\nTransaction of " + amount + " successful. \nYour old balance was: " + balanceFrom +
                        "\nYour new balance is " + newBalance;
            }
        } else {
            return "User not found";
        }
    }

}
