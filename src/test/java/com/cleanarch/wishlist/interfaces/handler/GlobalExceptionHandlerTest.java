package com.cleanarch.wishlist.interfaces.handler;

import com.cleanarch.wishlist.domain.exception.BusinessException;
import com.cleanarch.wishlist.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    // Como a classe não tem dependências (não usa injetores ou repositories),
    // podemos instanciá-la diretamente com o 'new' sem precisar do MockitoExtension.
    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    public void handleBusinessException_shouldReturnBadRequestWithExceptionMessage() {
        // 1. PREPARAÇÃO (Arrange)
        String errorMessage = "Limite de itens foi alcançado";
        BusinessException exception = new BusinessException(errorMessage);

        // 2. AÇÃO (Act)
        ResponseEntity<String> response = exceptionHandler.handleBusinessException(exception);

        // 3. VERIFICAÇÃO (Assert)
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    public void handleNotFoundException_shouldReturnNotFoundWithExceptionMessage() {
        // 1. PREPARAÇÃO (Arrange)
        String errorMessage = "Wishlist não encontrada";
        NotFoundException exception = new NotFoundException(errorMessage);

        // 2. AÇÃO (Act)
        ResponseEntity<String> response = exceptionHandler.handleNotFoundException(exception);

        // 3. VERIFICAÇÃO (Assert)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    public void handleIllegalArgumentException_shouldReturnBadRequestWithFormattedMessage() {
        // 1. PREPARAÇÃO (Arrange)
        String errorMessage = "customerId não pode ser nulo ou branco";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // 2. AÇÃO (Act)
        ResponseEntity<String> response = exceptionHandler.handleIllegalArgumentException(exception);

        // 3. VERIFICAÇÃO (Assert)
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Argumento inválido: customerId não pode ser nulo ou branco", response.getBody());
    }

    @Test
    public void handleNoHandlerFoundException_shouldReturnBadRequestWithStaticMessage() {
        // 1. PREPARAÇÃO (Arrange)
        // A NoHandlerFoundException do Spring precisa de alguns parâmetros no construtor para ser criada.
        NoHandlerFoundException exception = new NoHandlerFoundException("POST", "/api/wishlists//products", HttpHeaders.EMPTY);

        // 2. AÇÃO (Act)
        ResponseEntity<String> response = exceptionHandler.handleNoHandlerFoundException(exception);

        // 3. VERIFICAÇÃO (Assert)
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("O parâmetro requirido não está no path.", response.getBody());
    }
}