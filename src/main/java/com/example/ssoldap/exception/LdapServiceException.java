package com.example.ssoldap.exception;

/**
 * Exception thrown when LDAP/Active Directory operations fail
 */
public class LdapServiceException extends RuntimeException {
    
    public LdapServiceException(String message) {
        super(message);
    }
    
    public LdapServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}