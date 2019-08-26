package miw.s16.couch.couch.model;


public class User {

    private String userName;
    private String userPassword;
    private int userId;



    public User() {
        this("", "", 100);
    }


    public User(String userName, String userPassword, int userId) {
        super();
        this.userName = userName;
        this.userPassword = userPassword;
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
