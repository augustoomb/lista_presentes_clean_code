package com.cleanarch.wishlist.application.dto;

import java.util.Set;

public class ProductIdsResponse {

    private Set<String> productIds;

    public ProductIdsResponse(Set<String> productIds) {
        this.productIds = productIds;
    }

    public Set<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(Set<String> productIds) {
        this.productIds = productIds;
    }
}
