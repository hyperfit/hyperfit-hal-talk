package com.bodybuilding.haltalk;


import org.hyperfit.annotation.Link;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.controls.link.HyperLink;

public interface Users extends HyperResource {

    @Link(ContractConstants.REL_USER)
    HyperLink[] getUserLinks();
}
