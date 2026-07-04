package com.cleanarch.wishlist.domain.repository;

import com.cleanarch.wishlist.domain.entity.Wishlist;

import java.util.Optional;

public interface WishlistRepository {

    Optional<Wishlist>findByCustomerId(String customerId);

    void save(Wishlist wishlist);

    void deleteByCustomerId(String customerId);
}

/*
    O domínio diz: "Eu preciso salvar e buscar a Wishlist,
    mas eu não faço ideia de como isso é feito no banco".
    Quem quiser usar o domínio terá que implementar essa interface.
 */