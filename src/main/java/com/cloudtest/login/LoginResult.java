package com.cloudtest.login;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ysokolovski
 * Date: 21/11/13
 * Time: 3:12 PM
 */
public class LoginResult {
    private final boolean success;
    private final String msg;

    public LoginResult(boolean success) {
        this.success=success;
        this.msg="Success";
    }

    public LoginResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }
}
