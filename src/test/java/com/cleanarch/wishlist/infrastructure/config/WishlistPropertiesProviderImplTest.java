package com.cleanarch.wishlist.infrastructure.config;

import com.cleanarch.wishlist.infrastructure.persistence.ConfigPropertyDocument;
import com.cleanarch.wishlist.infrastructure.repository.ConfigPropertyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WishlistPropertiesProviderImplTest {

    @Mock
    private ConfigPropertyRepository configRepo;

    @InjectMocks
    private WishlistPropertiesProviderImpl wishlistPropertiesProvider;

    @Test
    public void getMaxProducts_shouldReturnDatabaseValue_whenKeyExistsAndIsValid() {
        // 1. PREPARAÇÃO (Arrange)
        String configKey = "wishlist.maxProducts";

        // Simula um documento do MongoDB com o valor "15"
        ConfigPropertyDocument documentMock = new ConfigPropertyDocument();
        documentMock.setKey(configKey);
        documentMock.setValue("15");

        when(configRepo.findByKey(configKey)).thenReturn(documentMock);

        // 2. AÇÃO (Act)
        int result = wishlistPropertiesProvider.getMaxProducts();

        // 3. VERIFICAÇÃO (Assert)
        // Garante que a String "15" foi convertida com sucesso para o int 15
        assertEquals(15, result);
    }

    @Test
    public void getMaxProducts_shouldReturnDefaultValue_whenValueInDatabaseIsNotANumber() {
        // 1. PREPARAÇÃO (Arrange)
        String configKey = "wishlist.maxProducts";

        // Simula um documento com valor textual inválido para conversão numérica
        ConfigPropertyDocument documentMock = new ConfigPropertyDocument();
        documentMock.setKey(configKey);
        documentMock.setValue("vinte");

        when(configRepo.findByKey(configKey)).thenReturn(documentMock);

        // 2. AÇÃO (Act)
        int result = wishlistPropertiesProvider.getMaxProducts();

        // 3. VERIFICAÇÃO (Assert)
        // Como "vinte" joga uma NumberFormatException, o catch deve segurar o erro
        // e o método deve retornar o valor padrão (fallback) que é 6.
        assertEquals(6, result);
    }

    @Test
    public void getMaxProducts_shouldReturnDefaultValue_whenKeyDoesNotExistInDatabase() {
        // 1. PREPARAÇÃO (Arrange)
        String configKey = "wishlist.maxProducts";

        // Simula que a configuração não existe no banco (retorna null)
        when(configRepo.findByKey(configKey)).thenReturn(null);

        // 2. AÇÃO (Act)
        int result = wishlistPropertiesProvider.getMaxProducts();

        // 3. VERIFICAÇÃO (Assert)
        // Se o documento é nulo, o if(prop != null) é ignorado e deve retornar 6.
        assertEquals(6, result);
    }
}