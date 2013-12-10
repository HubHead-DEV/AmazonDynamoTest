package com.cloudtest.login;

/**
 * Created with IntelliJ IDEA.
 * User: ysokolovski
 * Date: 21/11/13
 * Time: 11:55 AM
 */
public class Login {
    private final String username;
    private final String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
