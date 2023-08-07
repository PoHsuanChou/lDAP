package org.example;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import lombok.val;

public class Main {
    public static void main(String[] args) throws LDAPException {
        System.out.println("Hello world!");


        String ldapHost = "ldap.example.com";
        int ldapPort = 389; // Default LDAP port

        // Bind credentials for authentication (if required)
        String bindDN = "cn=admin,dc=example,dc=com";
        String bindPassword = "adminPassword";

        // Create an LDAP connection
        LDAPConnection ldapConnection = new LDAPConnection(ldapHost, ldapPort, bindDN, bindPassword);
    }
}