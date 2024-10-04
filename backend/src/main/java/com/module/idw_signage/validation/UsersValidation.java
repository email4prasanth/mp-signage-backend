package com.module.idw_signage.validation;

import com.module.idw_signage.Utils.Constants;
import org.springframework.stereotype.Component;

@Component
public class UsersValidation {
    public String saveUserValidation(String fullName, String email, String access, String[] stores ) {
        if(fullName == null || fullName.isEmpty()) {
            return Constants.USER_NAME_INVALID;
        }else if(email == null || email.isEmpty()) {
            return Constants.USER_EMAIL_INVALID;
        }else if (!email.contains("@") || !email.contains(".")) {
            return Constants.USER_EMAIL_INVALID_FORMAT;
        } else if (access == null || access.isEmpty()) {
            return Constants.USER_ACCESS_INVALID;
        }else if (stores.length == 0) {
            return Constants.USER_STORES_INVALID;
        } else {
            return null;
        }
    }
}
