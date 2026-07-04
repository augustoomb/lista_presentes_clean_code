package com.cleanarch.wishlist.infrastructure.config;

import com.cleanarch.wishlist.application.config.WishlistPropertiesProvider;
import com.cleanarch.wishlist.infrastructure.persistence.ConfigPropertyDocument;
import com.cleanarch.wishlist.infrastructure.repository.ConfigPropertyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class WishlistPropertiesProviderImpl implements WishlistPropertiesProvider {

    private static final Logger LOGGER = LogManager.getLogger(WishlistPropertiesProviderImpl.class);

    private final ConfigPropertyRepository configRepo;

    public WishlistPropertiesProviderImpl(ConfigPropertyRepository configRepo) {
        this.configRepo = configRepo;
    }

    @Override
    public int getMaxProducts() {

        LOGGER.info("Buscando valor de maxProducts no config Repository");

        ConfigPropertyDocument prop = configRepo.findByKey("wishlist.maxProducts");

        if(prop != null) {
            try {
                return Integer.parseInt(prop.getValue());
            } catch (NumberFormatException e) {
                // throw new RuntimeException(e);

                LOGGER.warn("Valor encontrado para maxProducts foi:{}", prop.getValue());

            }
        }

        return 6;
    }
}
