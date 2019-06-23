package com.friday.ar.plugin.security;

import java.security.GeneralSecurityException;

public class VerificationSecurityException extends GeneralSecurityException {
    public VerificationSecurityException(String msg) {
        super(msg);
    }
}
