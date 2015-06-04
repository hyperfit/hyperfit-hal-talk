package com.bodybuilding.haltalk;


import org.hyperfit.annotation.Link;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.controls.link.HyperLink;

public interface Posts extends HyperResource {

    @Link(ContractConstants.REL_POST)
    Post[] posts();
}
