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

    @Link(ContractConstants.REL_AUTHOR)
    HyperLink getAuthorLink();

    @Link(ContractConstants.REL_AUTHOR)
    Author author();

    @Link(ContractConstants.REL_IN_REPLY_TO)
    boolean isReply();

    @Link(ContractConstants.REL_IN_REPLY_TO)
    Post postRepliedTo();
}
