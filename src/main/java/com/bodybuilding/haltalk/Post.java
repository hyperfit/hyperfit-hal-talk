package com.bodybuilding.haltalk;


import org.hyperfit.annotation.Data;
import org.hyperfit.annotation.Link;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.controls.link.HyperLink;

public interface Post extends HyperResource {

    @Data(ContractConstants.DATA_FIELD_POST_CONTENT)
    String getContent();

    @Data(ContractConstants.DATA_FIELD_POST_CREATED_AT)
    String getCreated();
}
