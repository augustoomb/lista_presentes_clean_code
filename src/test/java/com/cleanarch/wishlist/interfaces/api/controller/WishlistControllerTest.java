package com.cleanarch.wishlist.interfaces.api.controller;

import com.cleanarch.wishlist.application.dto.ProductIdsResponse;
import com.cleanarch.wishlist.application.usecase.WishlistUseCase;
import com.cleanarch.wishlist.interfaces.api.dto.ProductIdsResponseDTO;
import com.cleanarch.wishlist.interfaces.api.dto.ResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WishlistControllerTest {

    @Mock
    private WishlistUseCase wishlistUseCase;

    @InjectMocks
    private WishlistController wishlistController;

    @Test
    public void addProduct_shouldReturnStatusCreated_whenProductIsAddedWithSuccess() {
        // 1. PREPARAÇÃO (Arrange)
        String customerId = "customer123";
        String productId = "product456";

        // Como o metodo do UseCase é void, usamos o doNothing()
        doNothing().when(wishlistUseCase).addProduct(customerId, productId);

        // 2. AÇÃO (Act)
        ResponseEntity<Void> response = wishlistController.addProduct(customerId, productId);

        // 3. VERIFICAÇÃO (Assert)
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody(), "O corpo da resposta de criação deve ser nulo/vazio");

        // Garante que o controller repassou os parâmetros idênticos para a camada de aplicação
        verify(wishlistUseCase).addProduct(customerId, productId);
    }

    @Test
    public void removeProduct_shouldReturnStatusNoContent_whenProductIsRemovedWithSuccess() {
        // 1. PREPARAÇÃO (Arrange)
        String customerId = "customer123";
        String productId = "product456";

        doNothing().when(wishlistUseCase).removeProduct(customerId, productId);

        // 2. AÇÃO (Act)
        ResponseEntity<Void> response = wishlistController.removeProduct(customerId, productId);

        // 3. VERIFICAÇÃO (Assert)
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(wishlistUseCase).removeProduct(customerId, productId);
    }

    @Test
    public void removeCustomerWishlist_shouldReturnStatusNoContent_whenWishlistIsDeletedWithSuccess() {
        // 1. PREPARAÇÃO (Arrange)
        String customerId = "customer123";

        doNothing().when(wishlistUseCase).removeCustomerWishlist(customerId);

        // 2. AÇÃO (Act)
        ResponseEntity<Void> response = wishlistController.removeCustomerWishlist(customerId);

        // 3. VERIFICAÇÃO (Assert)
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(wishlistUseCase).removeCustomerWishlist(customerId);
    }

    @Test
    public void getAllProducts_shouldReturnResponseDtoWithOkStatus_whenCustomerHasProducts() {
        // 1. PREPARAÇÃO (Arrange)
        String customerId = "customer123";
        Set<String> mockProducts = Set.of("product456", "product789");

        // Montamos o DTO simulado que o UseCase retornará para o Controller
        ProductIdsResponse useCaseResponse = new ProductIdsResponse(mockProducts);

        when(wishlistUseCase.getAllProducts(customerId)).thenReturn(useCaseResponse);

        // 2. AÇÃO (Act)
        ResponseEntity<ResponseDTO<ProductIdsResponseDTO>> response = wishlistController.getAllProducts(customerId);

        // 3. VERIFICAÇÃO (Assert)
        // Valida o encapsulamento do HttpStatus na resposta do Spring
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Valida se a estrutura interna do ResponseDTO foi montada conforme o esperado
        assertNotNull(response.getBody(), "O corpo do ResponseDTO não deveria ser nulo");
        assertEquals("Sucesso", response.getBody().getMessage());
        assertEquals(200, response.getBody().getStatus());

        // Valida se os IDs dos produtos dentro do ProductIdsResponseDTO são idênticos aos mockados
        ProductIdsResponseDTO dataBody = response.getBody().getData();
        assertNotNull(dataBody, "O objeto de dados interno (data) não deveria ser nulo");
        assertEquals(mockProducts, dataBody.getProductsIds());

        // Confirma a interação com a camada inferior de negócio
        verify(wishlistUseCase).getAllProducts(customerId);
    }
}