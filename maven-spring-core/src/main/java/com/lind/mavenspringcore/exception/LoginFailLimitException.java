package com.lind.mavenspringcore.exception;

import lombok.Getter;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

/**
 * @author Exrickx
 */
@Getter

public class LoginFailLimitException extends InternalAuthenticationServiceException {


    public LoginFailLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginFailLimitException(String message) {
        super(message);
    }
}
