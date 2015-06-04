package com.bodybuilding.haltalk;


import org.hyperfit.annotation.Data;
import org.hyperfit.resource.HyperResource;

public interface Author extends HyperResource {

    @Data(ContractConstants.DATA_FIELD_AUTHOR_USERNAME)
    String getUsername();

    @Data(ContractConstants.DATA_FIELD_AUTHOR_BIO)
    String getBio();

    @Data(ContractConstants.DATA_FIELD_AUTHOR_REAL_NAME)
    String getRealName();
}
