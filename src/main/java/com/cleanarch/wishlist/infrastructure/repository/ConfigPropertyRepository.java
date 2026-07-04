package com.cleanarch.wishlist.infrastructure.repository;

import com.cleanarch.wishlist.infrastructure.persistence.ConfigPropertyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigPropertyRepository extends MongoRepository<ConfigPropertyDocument,String> {

    ConfigPropertyDocument findByKey(String key);

}
