package com.cleanarch.wishlist.infrastructure.repository;

import com.cleanarch.wishlist.domain.entity.Wishlist;
import com.cleanarch.wishlist.infrastructure.persistence.WishlistDocument;
import com.cleanarch.wishlist.infrastructure.persistence.WishlistMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WishlistRepositoryMongoImplTest {

    @Mock
    private WishlistMongoSpringData mongoRepo;

    @Mock
    private WishlistMapper wishlistMapper;

    @InjectMocks
    private WishlistRepositoryMongoImpl wishlistRepositoryMongo;

    @Test
    public void findByCustomerId_shouldReturnOptionalWithDomain_whenDocumentExists() {
        // 1. PREPARAÇÃO (Arrange)
        String customerId = "customer123";

        // CORREÇÃO: Utilizando o construtor correto do WishlistDocument
        WishlistDocument documentMock = new WishlistDocument("doc-id-123", customerId, Set.of("product456"));
        Wishlist wishlistDomainMock = new Wishlist(null, customerId, null);

        when(mongoRepo.findByCustomerId(customerId)).thenReturn(Optional.of(documentMock));
        when(wishlistMapper.toDomain(documentMock)).thenReturn(wishlistDomainMock);

        // 2. AÇÃO (Act)
        Optional<Wishlist> result = wishlistRepositoryMongo.findByCustomerId(customerId);

        // 3. VERIFICAÇÃO (Assert)
        assertTrue(result.isPresent(), "O resultado deveria conter uma Wishlist");
        assertEquals(customerId, result.get().getCustomerId());
        verify(mongoRepo).findByCustomerId(customerId);
        verify(wishlistMapper).toDomain(documentMock);
    }

    @Test
    public void findByCustomerId_shouldReturnOptionalEmpty_whenDocumentDoesNotExist() {
        // 1. PREPARAÇÃO (Arrange)
        String customerId = "customer123";

        when(mongoRepo.findByCustomerId(customerId)).thenReturn(Optional.empty());

        // 2. AÇÃO (Act)
        Optional<Wishlist> result = wishlistRepositoryMongo.findByCustomerId(customerId);

        // 3. VERIFICAÇÃO (Assert)
        assertFalse(result.isPresent(), "O resultado deveria ser um Optional vazio");
        verifyNoInteractions(wishlistMapper);
    }

    @Test
    public void save_shouldMapToDocumentAndSaveInMongo() {
        // 1. PREPARAÇÃO (Arrange)
        Wishlist wishlistDomain = new Wishlist(null, "customer123", null);

        // CORREÇÃO: Passando argumentos para o construtor (podem ser mockados ou vazios aqui)
        WishlistDocument documentMock = new WishlistDocument("doc-id-123", "customer123", Collections.emptySet());

        when(wishlistMapper.toDocument(wishlistDomain)).thenReturn(documentMock);

        // 2. AÇÃO (Act)
        wishlistRepositoryMongo.save(wishlistDomain);

        // 3. VERIFICAÇÃO (Assert)
        verify(wishlistMapper).toDocument(wishlistDomain);
        verify(mongoRepo).save(documentMock);
    }

    @Test
    public void deleteByCustomerId_shouldCallMongoRepoDelete() {
        // 1. PREPARAÇÃO (Arrange)
        String customerId = "customer123";

        // 2. AÇÃO (Act)
        wishlistRepositoryMongo.deleteByCustomerId(customerId);

        // 3. VERIFICAÇÃO (Assert)
        verify(mongoRepo).deleteByCustomerId(customerId);
    }
}