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

}
