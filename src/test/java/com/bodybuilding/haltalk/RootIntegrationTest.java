package com.bodybuilding.haltalk;

import org.hyperfit.HyperfitProcessor;
import org.hyperfit.content.ContentType;
import org.hyperfit.content.hal.json.HalJsonContentTypeHandler;
import org.hyperfit.net.BoringRequestBuilder;
import org.hyperfit.net.RequestBuilder;
import org.hyperfit.net.okhttp2.OkHttp2HyperClient;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.controls.link.HyperLink;
import org.junit.Test;
import utils.Helpers;

public class RootIntegrationTest {

    @Test
    public void testRootRetrievalForcedHeader() {
        HyperfitProcessor processor = new HyperfitProcessor.Builder()
            .addContentTypeHandler(new HalJsonContentTypeHandler())
            .hyperClient(new OkHttp2HyperClient(Helpers.allTrustingOkHttpClient()))
            .build();

        BoringRequestBuilder request = BoringRequestBuilder.get(ContractConstants.rootURL)
            .addAcceptedContentType("application/json")
            ;

        HyperResource resource = processor.processRequest(HyperResource.class, request);

    }

    @Test
    public void testRootRetrieval() {
        HyperfitProcessor processor = new HyperfitProcessor.Builder()
        .addContentTypeHandler(
            new HalJsonContentTypeHandler(),
            new ContentType("application", "json")
        )
        .hyperClient(new OkHttp2HyperClient(Helpers.allTrustingOkHttpClient()))
        .build();


        HyperResource resource = processor.processRequest(HyperResource.class, ContractConstants.rootURL);

        System.out.println(resource.toString());

        System.out.println("\nLinks:");
        for(org.hyperfit.resource.controls.link.HyperLink link : resource.getLinks()){
            System.out.println(link.getRel() + " => " + link.getHref());
        }

        System.out.println("\nData:");
        String welcome = resource.getPathAs(String.class, ContractConstants.DATA_FIELD_ROOT_WELCOME);
        System.out.println("Welcome: " + welcome);



    }


    @Test
    public void testUserRetrieval() {
        HyperfitProcessor processor = HALTalkProcessor.builder()
        .hyperClient(new OkHttp2HyperClient(Helpers.allTrustingOkHttpClient()))
        .build();


        HyperResource resource = processor.processRequest(HyperResource.class, ContractConstants.rootURL);

        RequestBuilder usersRequest = resource.getLink(ContractConstants.REL_USERS).toRequestBuilder();

        HyperResource usersResource = processor.processRequest(HyperResource.class, usersRequest);



        System.out.println("\nLinks:");
        for(org.hyperfit.resource.controls.link.HyperLink link : usersResource.getLinks()){
            System.out.println(link.getRel() + " => " + link.getHref());
        }


    }

    @Test
    public void testRootRetrievalWithInterface() {
        HyperfitProcessor processor = new HyperfitProcessor.Builder()
        .addContentTypeHandler(
            new HalJsonContentTypeHandler(),
            new ContentType("application", "json")
        )
        .hyperClient(new OkHttp2HyperClient(Helpers.allTrustingOkHttpClient()))
        .build();


        Root resource = processor.processRequest(Root.class, ContractConstants.rootURL);

        System.out.println(resource.toString());

        System.out.println("\nLinks:");
        for(org.hyperfit.resource.controls.link.HyperLink link : resource.getLinks()){
            System.out.println(link.getRel() + " => " + link.getHref());
        }

        System.out.println("\nData:");
        System.out.println("Welcome: " + resource.getWelcome());
        System.out.println("Hint 1: " + resource.getHint1());
        System.out.println("Hint 2: " + resource.getHint2());
        System.out.println("Hint 3: " + resource.getHint3());
        System.out.println("Hint 4: " + resource.getHint4());
        System.out.println("Hint 5: " + resource.getHint5());
    }




    }
