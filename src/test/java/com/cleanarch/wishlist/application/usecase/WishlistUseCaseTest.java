package com.cleanarch.wishlist.application.usecase;

import com.cleanarch.wishlist.application.config.WishlistPropertiesProvider;
import com.cleanarch.wishlist.application.dto.ProductIdsResponse;
import com.cleanarch.wishlist.domain.entity.Wishlist;
import com.cleanarch.wishlist.domain.exception.BusinessException;
import com.cleanarch.wishlist.domain.exception.NotFoundException;
import com.cleanarch.wishlist.domain.repository.WishlistRepository;
import com.cleanarch.wishlist.domain.vo.ProductId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Teste padrão deve ter: AAA (Arrange, Act, Assert)

@ExtendWith(MockitoExtension.class) // Avisa ao JUnit 5 que usaremos o Mockito para gerenciar os dublês de teste neste arquivo.
public class WishlistUseCaseTest {

    // Cria um banco de dados "de mentira". Nós vamos dizer para ele o que retornar quando a classe principal fizer uma busca.
    @Mock
    private WishlistRepository wishlistRepository;

    // Cria um provedor de configurações "de mentira" para simularmos o limite máximo de itens na lista.
    @Mock
    private WishlistPropertiesProvider wishlistPropertiesProvider;

    // Cria a sua classe real que queremos testar e, automaticamente, injeta dentro dela os dois dublês (Mocks) criados acima.
    @InjectMocks
    private WishlistUseCase wishlistUseCase;

    @Test
    public void addProduct_sholdThrow_whenCustomerIdIsBlank() {
        // 1. PREPARAÇÃO (Arrange)
        // Aqui definimos um cenário onde o customerId está em branco e um productId válido.
        String customerId = "   ";
        String productId = "product456";

        // NOTA: Como a validação acontece logo no início do método, o fluxo será interrompido
        // ANTES de interagir com qualquer Mock. Portanto, não precisamos configurar nenhum 'when(...)'.

        // 2 e 3. AÇÃO E VERIFICAÇÃO (Act & Assert)
        // O assertThrows intercepta a execução e valida se a exceção correta foi lançada.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            wishlistUseCase.addProduct(customerId, productId);
        });

        // Opcional: Garante que a mensagem da exceção é exatamente a que você escreveu no UseCase
        assertEquals("customerId não pode ser nulo ou branco", exception.getMessage());

        // Garante que o banco de dados sequer foi consultado, já que o código falhou antes
        verifyNoInteractions(wishlistRepository);
    }

    @Test
    public void addProduct_sholdThrow_whenCustomerIdIsNull() {
        // 1. PREPARAÇÃO (Arrange)
        String customerId = null; // Cenário com ID do cliente nulo
        String productId = "product456";

        // 2 & 3. AÇÃO E VERIFICAÇÃO (Act & Assert)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            wishlistUseCase.addProduct(customerId, productId);
        });

        assertEquals("customerId não pode ser nulo ou branco", exception.getMessage());
        verifyNoInteractions(wishlistRepository);
    }

    @Test
    public void addProduct_sholdThrow_whenProductIdIsBlank() {
        // 1. PREPARAÇÃO (Arrange)
        String customerId = "customer123";
        String productId = "   "; // Cenário com ID do produto em branco (espaços)

        // 2 & 3. AÇÃO E VERIFICAÇÃO (Act & Assert)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            wishlistUseCase.addProduct(customerId, productId);
        });

        assertEquals("productId não pode ser nulo ou branco", exception.getMessage());
        verifyNoInteractions(wishlistRepository);
    }

    @Test
    public void addProduct_sholdThrow_whenProductIdIsNull() {
        // 1. PREPARAÇÃO (Arrange)
        String customerId = "customer123";
        String productId = null; // Cenário com ID do produto nulo

        // 2 & 3. AÇÃO E VERIFICAÇÃO (Act & Assert)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            wishlistUseCase.addProduct(customerId, productId);
        });

        assertEquals("productId não pode ser nulo ou branco", exception.getMessage());
        verifyNoInteractions(wishlistRepository);
    }

    @Test
    public void testAddProduct_Success() {
        // 1. PREPARAÇÃO (Arrange)
        String customerId = "customer123";
        String productId = "product456";

        // Aqui dizemos: "Quando o usecase buscar a wishlist no banco, finja que não achou nada (Optional.empty)"
        when(wishlistRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        // "Quando o usecase perguntar o limite máximo, responda que é 10"
        when(wishlistPropertiesProvider.getMaxProducts()).thenReturn(10);

        // 2. AÇÃO (Act)
        // Executa o metodo real com os parâmetros que preparamos
        wishlistUseCase.addProduct(customerId, productId);

        // 3. VERIFICAÇÃO (Assert)
        // Garante que, ao final do processo, o metodo .save() do banco de dados foi chamado com uma Wishlist qualquer
        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    public void testAddProduct_ProductAlreadyExists() {
        String customerId = "customer123";
        String productId = "product456";

        // Usando new HashSet para permitir mutabilidade se necessário, evitando imutabilidade estrita do Set.of
        Wishlist existingWishlist = new Wishlist(null, customerId, new HashSet<>(Set.of(new ProductId(productId))));
        when(wishlistRepository.findByCustomerId(customerId)).thenReturn(Optional.of(existingWishlist));

        assertThrows(BusinessException.class, () -> {
            wishlistUseCase.addProduct(customerId, productId);
        });
    }

    @Test
    public void testAddProduct_MaxProductsLimitReached() {
        String customerId = "customer123";
        String productId = "product456";

        Wishlist existingWishlist = new Wishlist(null, customerId, new HashSet<>(Set.of(new ProductId("product789"), new ProductId("product012"))));
        when(wishlistRepository.findByCustomerId(customerId)).thenReturn(Optional.of(existingWishlist));
        when(wishlistPropertiesProvider.getMaxProducts()).thenReturn(2);

        assertThrows(BusinessException.class, () -> {
            wishlistUseCase.addProduct(customerId, productId);
        });
    }

    @Test
    public void testRemoveProduct_Success() {
        String customerId = "customer123";
        String productId = "product456";

        // CORREÇÃO: Encapsulado em new HashSet<> para evitar UnsupportedOperationException no .remove()
        Wishlist existingWishlist = new Wishlist(null, customerId, new HashSet<>(Set.of(new ProductId(productId))));
        when(wishlistRepository.findByCustomerId(customerId)).thenReturn(Optional.of(existingWishlist));

        wishlistUseCase.removeProduct(customerId, productId);

        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    public void testRemoveProduct_WishlistNotFound() {
        String customerId = "customer123";
        String productId = "product456";

        when(wishlistRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            wishlistUseCase.removeProduct(customerId, productId);
        });
    }

    @Test
    public void testRemoveProduct_ProductNotFoundInExistingWishlist() {
        String customerId = "customer123";
        String productId = "product456";

        Wishlist existingWishlist = new Wishlist(null, customerId, new HashSet<>(Set.of(new ProductId("outro-produto"))));
        when(wishlistRepository.findByCustomerId(customerId)).thenReturn(Optional.of(existingWishlist));

        assertThrows(NotFoundException.class, () -> {
            wishlistUseCase.removeProduct(customerId, productId);
        });
    }

    @Test
    public void testRemoveCustomerWishlist_Success() {
        String customerId = "customer123";

        Wishlist existingWishlist = new Wishlist(null, customerId, new HashSet<>(Set.of(new ProductId("product456"))));
        when(wishlistRepository.findByCustomerId(customerId)).thenReturn(Optional.of(existingWishlist));

        wishlistUseCase.removeCustomerWishlist(customerId);

        verify(wishlistRepository).deleteByCustomerId(customerId);
    }

    @Test
    public void testRemoveCustomerWishlist_WishlistNotFound() {
        String customerId = "customer123";

        when(wishlistRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            wishlistUseCase.removeCustomerWishlist(customerId);
        });
    }

    @Test
    public void testGetAllProducts_Success() {
        String customerId = "customer123";
        Set<String> productIds = Set.of("product456", "product789");

        Wishlist existingWishlist = new Wishlist(null, customerId, productIds.stream().map(ProductId::new).collect(Collectors.toSet()));
        when(wishlistRepository.findByCustomerId(customerId)).thenReturn(Optional.of(existingWishlist));

        ProductIdsResponse response = wishlistUseCase.getAllProducts(customerId);

        assertEquals(productIds, response.getProductIds());
    }
}
