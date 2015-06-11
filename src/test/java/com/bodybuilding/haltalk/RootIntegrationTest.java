package com.bodybuilding.haltalk;

import org.hyperfit.HyperfitProcessor;
import org.hyperfit.content.hal.json.HalJsonContentTypeHandler;
import org.hyperfit.net.okhttp2.OkHttp2HyperClient;
import org.hyperfit.resource.HyperResource;
import org.junit.Test;
import utils.Helpers;

public class RootIntegrationTest {

    @Test
    public void testRootRetrieval() {
        HyperfitProcessor processor = new HyperfitProcessor.Builder()
            .addContentTypeHandler(new HalJsonContentTypeHandler())
            .hyperClient(new OkHttp2HyperClient(Helpers.allTrustingOkHttpClient()))
            .build();

        HyperResource resource = processor.processRequest(HyperResource.class, ContractConstants.rootURL);

    }




}
