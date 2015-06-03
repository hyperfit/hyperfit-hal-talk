package com.bodybuilding.haltalk;

import org.hyperfit.HyperfitProcessor;
import org.hyperfit.content.hal.json.HalJsonContentTypeHandler;
import org.junit.Test;

public class RootIntegrationTest {

    @Test
    public void testRootRetrieval() {
        HyperfitProcessor processor = new HyperfitProcessor.Builder()
            .addContentTypeHandler(new HalJsonContentTypeHandler())
            .build();


    }




}
