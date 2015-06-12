package com.bodybuilding.haltalk;

/**
 * Collection of static constants that are part of the service's contract
 * If these things change then the service has made backwards incompatible changes
 */
public class ContractConstants {
    public static final String rootURL = "https://haltalk.herokuapp.com/";

    public static final String REL_USER = "ht:user";
    public static final String REL_USERS = "ht:users";

    public static final String REL_LATEST_POSTS = "ht:latest-posts";
    public static final String REL_POST = "ht:post";

    public static final String REL_AUTHOR = "ht:author";

    public static final String DATA_FIELD_ROOT_WELCOME = "welcome";
    public static final String DATA_FIELD_ROOT_HINT_1 = "hint_1";
    public static final String DATA_FIELD_ROOT_HINT_2 = "hint_2";
    public static final String DATA_FIELD_ROOT_HINT_3 = "hint_3";
    public static final String DATA_FIELD_ROOT_HINT_4 = "hint_4";
    public static final String DATA_FIELD_ROOT_HINT_5 = "hint_5";


    public static final String DATA_FIELD_POST_CONTENT = "content";
    public static final String DATA_FIELD_POST_CREATED_AT = "created_at";



}
