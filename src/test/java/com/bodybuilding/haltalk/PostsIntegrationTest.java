package com.bodybuilding.haltalk;

import org.hyperfit.HyperfitProcessor;
import org.hyperfit.content.hal.json.HalJsonContentTypeHandler;
import org.hyperfit.net.BoringRequestBuilder;
import org.hyperfit.net.RequestBuilder;
import org.hyperfit.net.okhttp2.OkHttp2HyperClient;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.controls.link.HyperLink;
import org.junit.Test;
import utils.Helpers;

public class PostsIntegrationTest {




    @Test
    public void testRetrieveLatestPosts() {

        Root root = Helpers.fetchRoot();

        //No more need to specify reflection class<t> stuff!
        Posts posts = root.latestPosts();


        for(Post post : posts.posts()){
            System.out.println(post.getAuthorLink().getTitle() + " posted " + post.getContent() + " @ " + post.getCreated());
        }


    }


    @Test
    public void testRetrieveLatestPostsWithAuthorData() {

        Root root = Helpers.fetchRoot();

        //No more need to specify reflection class<t> stuff!
        Posts posts = root.latestPosts();


        for(Post post : posts.posts()){
            Author author = post.author();//Network call!

            System.out.println(author.getRealName() + "[" + author.getUsername() + "]" + " posted " + post.getContent() + " @ " + post.getCreated());
        }


    }


    @Test
    public void testRetrievePostsOfLast10Posters() {

        Root root = Helpers.fetchRoot();

        //No more need to specify reflection class<t> stuff!
        Post[] latestPosts = root.latestPosts().posts();

        for(int i = 0; i < 10; i++){
            Author author = latestPosts[i].author();
            System.out.println("\n\nPosts by " + author.getRealName() + "[" + author.getUsername() + "]");

            for(Post post : author.posts().posts()){
                System.out.println(post.getContent() + " @ " + post.getCreated());
            }
        }


    }


    @Test
    public void testFindPostsThatAreReplies() {

        Root root = Helpers.fetchRoot();

        //No more need to specify reflection class<t> stuff!
        Post[] latestPosts = root.latestPosts().posts();

        for(Post post : latestPosts){
            if(post.isReply()){
                System.out.println(post.getAuthorLink().getTitle() + " replied " + post.getContent() + " @ " + post.getCreated());
            }

        }


    }

}
