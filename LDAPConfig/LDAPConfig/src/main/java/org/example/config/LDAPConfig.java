package org.example.config;

import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.unboundid.ldap.sdk.Filter;

public class LDAPConfig {

    public static final String OBJECT_CLASS = "objectClass";

    private LDAPConfig(){

    }

    public static LDAPConfig builder() {
        return new LDAPConfig();
    }

    private String ldapHost;
    private int ldapPort;
    private final DNInfo bindDN = new DNInfo(this);
    private String bindPassword;
    private final DNInfo searchBaseDN = new DNInfo(this);
    private final SSLInfo sslInfo = new SSLInfo(this);
    private final UserInfo userInfo = new UserInfo(this);


    public UserInfo userInfo() {
        return userInfo;
    }

    public String ldapHost() {
        return ldapHost;
    }

    public int ldapPort() {
        return ldapPort;
    }



    public String bindPassword() {
        return bindPassword;
    }

    public LDAPConfig ldapHost(String ldapHost){
        this.ldapHost = ldapHost;
        return this;
    }

    public DNInfo bindDN() {
        return bindDN;
    }

    public LDAPConfig bindPassword(String bindPassword) {
        this.bindPassword = bindPassword;
        return this;
    }

    public DNInfo searchBaseDN() {
        return searchBaseDN;
    }

    public LDAPConfig build() {
        return this;
    }

    public static abstract class SubInfo {
        protected final LDAPConfig ldapConfig;

        protected SubInfo(LDAPConfig ldapConfig) {
            this.ldapConfig = ldapConfig;
        }

        public LDAPConfig and(){ return ldapConfig; }

    }


    public static class UserInfo extends SubInfo {
        public static final String UID = "uid";
        public static final String USER_PASSWORD = "userPassword";

        protected UserInfo(LDAPConfig ldapConfig) {
            super(ldapConfig);
        }

        private Set<String> objectClasses;
        private Set<String> theAttributes;
        private String username;
        private String usernameAttrName;
        private String userPasswordAttrName;

        public UserInfo theAttributes(@NonNull String... theAttributes) {
            if (theAttributes.length != 0) {
                Collections.addAll(getTheAttributes(), theAttributes);
            }
            return this;
        }

        public UserInfo usernameAttrName(String usernameAttrName) {
            this.usernameAttrName = usernameAttrName;
            return this;
        }

        public String usernameAttrName() {
            return Optional.ofNullable(usernameAttrName)
                    .orElse(UID);
        }

        public UserInfo userPasswordAttrName(String userPasswordAttrName) {
            this.userPasswordAttrName = userPasswordAttrName;
            return this;
        }

        public String userPasswordAttrName() {
            return Optional.ofNullable(userPasswordAttrName)
                    .orElse(USER_PASSWORD);
        }

        public UserInfo username(String username) {
            this.username = username;
            return this;
        }


        public UserInfo objectClasses(@NonNull String... objectClasses) {
            if (objectClasses.length != 0) {
                Collections.addAll(getObjectClasses(), objectClasses);
            }
            return this;
        }


        public String[] theAttributes(){
            val emptyStringArray = new String[0];
            return theAttributes == null ?
                    emptyStringArray :
                    theAttributes.toArray(emptyStringArray);
        }

        public Filter filter() {
            val allFilterList = new ArrayList<Filter>();

            if (username != null) {
                allFilterList.add(Filter.createEqualityFilter(usernameAttrName(), username));
            }

            val objectClass = objectClasses.stream()
                    .map((objClassValue) -> Filter.createEqualityFilter(OBJECT_CLASS, objClassValue))
                    .collect(Collectors.toList());

            allFilterList.addAll(objectClass);

            return Filter.createANDFilter(allFilterList);
        }




        private Set<String> getTheAttributes() {
            if (theAttributes == null) {
                theAttributes = new HashSet<>();
            }
            return theAttributes;
        }

        public Set<String> getObjectClasses() {
            if (objectClasses == null) {
                objectClasses = new HashSet<>();
            }
            return objectClasses;
        }

    }

    public static class SSLInfo extends SubInfo {

        protected SSLInfo(LDAPConfig ldapConfig) {
            super(ldapConfig);
        }
        //TODO to be continue....

    }

    @Value
    public static class AttrInfo {
        @NonNull
        String name;

        @NonNull
        String value;
    }


    public static class DNInfo extends SubInfo{

        private List<AttrInfo> attrInfoList;

        private Comparator<AttrInfo> comparator;
        private String dn;

        protected DNInfo(LDAPConfig ldapConfig) {
            super(ldapConfig);
        }


        public DNInfo dn(String dn) {
            if (attrInfoList != null) {
                throw new RuntimeException("attr and dn can only choose one");
            }
            this.dn = dn;
            return this;
        }

        public DNInfo comparator(Comparator<AttrInfo> comparator) {
            this.comparator = comparator;
            return this;
        }

        public DNInfo attr(String name, String value) {
            if (dn != null) {
                throw new RuntimeException("attr and dn can only choose one");
            }

            getAttrInfoList().add(new AttrInfo(name, value));

            return this;
        }


        private List<AttrInfo> getAttrInfoList() {
            if (attrInfoList == null) {
                attrInfoList = new ArrayList<>();
            }
            return attrInfoList;
        }


        public String dn(){
            if(dn !=null){
                return dn;
            }

            if(attrInfoList == null){
                throw new RuntimeException("dn problem");
            }


            Stream<AttrInfo> infoStream = attrInfoList.stream();

            try(val attrInfoStream = infoStream){
                return attrInfoStream.map((attrInfo)->
                                    attrInfo.getName()
                                    + "="
                                    + attrInfo.getValue()
                        )
                        .collect(Collectors.joining(","));
            }

        }

    }

}
