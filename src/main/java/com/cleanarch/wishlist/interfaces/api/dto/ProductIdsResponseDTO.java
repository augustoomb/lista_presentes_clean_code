package com.cleanarch.wishlist.interfaces.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class ProductIdsResponseDTO {

    @JsonProperty("products_ids")
    private Set<String> productsIds;

    public ProductIdsResponseDTO(Set<String> productsIds) {
        this.productsIds = productsIds;
    }

    public Set<String> getProductsIds() {
        return productsIds;
    }

    public void setProductsIds(Set<String> productsIds) {
        this.productsIds = productsIds;
    }
}
