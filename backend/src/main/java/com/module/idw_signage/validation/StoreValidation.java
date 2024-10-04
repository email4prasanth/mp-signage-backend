package com.module.idw_signage.validation;

import com.module.idw_signage.Utils.Constants;
import com.module.idw_signage.dto.StoreDTO;
import org.springframework.stereotype.Component;

@Component
public class StoreValidation {

    public String saveStoreValidation(String storeName, String storeCatetory, String storeLocation) {
        if(storeName == null || storeName.isEmpty()) {
            return Constants.STORE_NAME_INVALID;
        }else if(storeCatetory == null || storeCatetory.isEmpty()) {
            return Constants.STORE_CATEGORY_INVALID;
        }else if (storeLocation == null || storeLocation.isEmpty()) {
            return Constants.STORE_LOCATION_INVALID;
        }else {
            return null;
        }
    }
}
