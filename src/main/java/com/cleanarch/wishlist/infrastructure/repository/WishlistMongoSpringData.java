package com.cleanarch.wishlist.infrastructure.repository;

import com.cleanarch.wishlist.infrastructure.persistence.WishlistDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WishlistMongoSpringData extends MongoRepository<WishlistDocument, String> {

    Optional<WishlistDocument> findByCustomerId(String customer);

    void deleteByCustomerId(String customerId);
}
