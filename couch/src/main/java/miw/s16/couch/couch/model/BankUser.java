package miw.s16.couch.couch.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class BankUser extends User {

    private String role;
    @OneToMany(mappedBy = "bankAccountTo")
    private List<Transaction> transactionsTo;


    public BankUser() {
    }

    public BankUser(String userName, String password, String role) {
        super(userName, password);
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
