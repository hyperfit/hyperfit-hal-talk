package com.bodybuilding.haltalk;

import org.junit.Test;
import utils.Helpers;

public class AuthorIntegrationTest {




    @Test
    public void testRetrieveAuthor() {

        Root root = Helpers.fetchRoot();

        //No more need to specify reflection class<t> stuff!
        Author user = root.user("drdamour");


        System.out.println("User: " + user.getUsername());
        System.out.println("Name: " + user.getRealName());
        System.out.println("Bio: " + user.getBio());

        int posts = user.posts().posts().length;
        System.out.println("Posts: " + posts);
    }




}
