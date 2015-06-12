package com.bodybuilding.haltalk;

import org.hyperfit.annotation.Data;
import org.hyperfit.resource.HyperResource;

public interface Root extends HyperResource {

    @Data(ContractConstants.DATA_FIELD_ROOT_WELCOME)
    String getWelcome();

    @Data(ContractConstants.DATA_FIELD_ROOT_HINT_1)
    String getHint1();

    @Data(ContractConstants.DATA_FIELD_ROOT_HINT_2)
    String getHint2();

    @Data(ContractConstants.DATA_FIELD_ROOT_HINT_3)
    String getHint3();

    @Data(ContractConstants.DATA_FIELD_ROOT_HINT_4)
    String getHint4();

    @Data(ContractConstants.DATA_FIELD_ROOT_HINT_5)
    String getHint5();

}
