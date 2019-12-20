package com.hnu.vo;

import com.hnu.Validator.isMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

//登陆的参数封装
public class LoginVo {

    @NotNull
    @isMobile
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String  password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginVo[" +
                "mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                ']';
    }
}
