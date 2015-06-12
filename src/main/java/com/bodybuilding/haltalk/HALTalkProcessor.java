package com.bodybuilding.haltalk;

import org.hyperfit.HyperfitProcessor;
import org.hyperfit.content.ContentType;
import org.hyperfit.content.hal.json.HalJsonContentTypeHandler;
import org.hyperfit.net.RequestInterceptor;

public class HALTalkProcessor {
    public static HyperfitProcessor.Builder builder(){
        return new HyperfitProcessor.Builder()
            .addContentTypeHandler(
            new HalJsonContentTypeHandler(),
            new ContentType("application", "json")
            )
        ;
    }


}
