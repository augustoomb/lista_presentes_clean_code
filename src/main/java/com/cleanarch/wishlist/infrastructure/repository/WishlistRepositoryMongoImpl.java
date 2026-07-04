package com.cleanarch.wishlist.infrastructure.repository;

import com.cleanarch.wishlist.domain.entity.Wishlist;
import com.cleanarch.wishlist.domain.repository.WishlistRepository;
import com.cleanarch.wishlist.infrastructure.persistence.WishlistDocument;
import com.cleanarch.wishlist.infrastructure.persistence.WishlistMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class WishlistRepositoryMongoImpl implements WishlistRepository {

    private final WishlistMongoSpringData mongoRepo;
    private final WishlistMapper wishlistMapper;

    public WishlistRepositoryMongoImpl(WishlistMongoSpringData mongoRepo, WishlistMapper wishlistMapper) {

        this.mongoRepo = mongoRepo;
        this.wishlistMapper = wishlistMapper;
    }

    @Override
    public Optional<Wishlist> findByCustomerId(String customerId) {

        return mongoRepo.findByCustomerId(customerId)
                .map(wishlistMapper::toDomain);
                // .map(wishlistEntity -> wishlistMapper.toDomain(wishlistEntity));
    }

    @Override
    public void save(Wishlist wishlist) {
        WishlistDocument doc = wishlistMapper.toDocument(wishlist);
        mongoRepo.save(doc);
    }

    @Override
    public void deleteByCustomerId(String customerId) {
        mongoRepo.deleteByCustomerId(customerId);
    }
}
