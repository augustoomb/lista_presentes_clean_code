package com.cleanarch.wishlist.infrastructure.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "wishlists")
public class WishlistDocument {


    @Id
    private String id;

    private String customerId;
    private Set<String> productIds;

    public WishlistDocument(String id, String customerId, Set<String> productIds) {
        this.id = id;
        this.customerId = customerId;
        this.productIds = productIds;
    }

    public String getId() {
        return id;
    }

    // 8:08

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Set<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(Set<String> productIds) {
        this.productIds = productIds;
    }
}
