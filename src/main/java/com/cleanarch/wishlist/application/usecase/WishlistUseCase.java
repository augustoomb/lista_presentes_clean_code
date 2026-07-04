package com.cleanarch.wishlist.application.usecase;

import com.cleanarch.wishlist.application.config.WishlistPropertiesProvider;
import com.cleanarch.wishlist.application.dto.ProductIdsResponse;
import com.cleanarch.wishlist.domain.entity.Wishlist;
import com.cleanarch.wishlist.domain.exception.BusinessException;
import com.cleanarch.wishlist.domain.exception.NotFoundException;
import com.cleanarch.wishlist.domain.repository.WishlistRepository;
import com.cleanarch.wishlist.domain.vo.ProductId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class WishlistUseCase {

    private final WishlistRepository wishlistRepository;

    private final WishlistPropertiesProvider wishlistPropertiesProvider;

    public WishlistUseCase(WishlistRepository wishlistRepository, WishlistPropertiesProvider wishlistPropertiesProvider) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistPropertiesProvider = wishlistPropertiesProvider;
    }

    public void addProduct(String customerId, String productId) {

        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId)
                .orElseGet(() -> new Wishlist(null, customerId, new HashSet<>()));

        // validar aqui se o produto já existe na lista
        if (wishlist.containsProduct(new ProductId(productId))){
            throw new BusinessException("Produto já existe na wishlist");
        }

        // validar aqui tamanho máximo da lista de desejos
        if (wishlist.canAddProduct(wishlistPropertiesProvider.getMaxProducts())) {
            throw new BusinessException("Limite de itens foi alcançado");
        }

        wishlist.getProductIds().add(new ProductId(productId));

        wishlistRepository.save(wishlist);
    }

    public void removeProduct(String customerId, String productId) {

        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Wishlist não encontrada"));

        if (!wishlist.getProductIds().contains(new ProductId(productId))){
            throw new NotFoundException("Produto não encontrado na wishlist");
        }

        wishlist.getProductIds().remove(new ProductId(productId));

        wishlistRepository.save(wishlist);
    }

    public void removeCustomerWishlist(String customerId) {
        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Wishlist não encontrada"));

        wishlistRepository.deleteByCustomerId(wishlist.getCustomerId());

    }

    public ProductIdsResponse getAllProducts(String customerId) {
        Optional<Wishlist> allProductsByCustomerId = wishlistRepository.findByCustomerId(customerId);

        Set<ProductId> productIds = allProductsByCustomerId
                .map(Wishlist::getProductIds) // PARA CADA ITEM DO MAP, FAÇA: Wishlist.getProductIds
                .orElse(Collections.emptySet());

        Set<String> ids = productIds.stream()
                .map(ProductId::toString)
                .collect(Collectors.toSet());


        return new ProductIdsResponse(ids);
    }


}

/*
usecase.WishlistUseCase: Equivale ao seu antigo Service.
Ele recebe a interface do repositório (via inversão de dependência),
 busca a entidade, executa a regra de negócio
 (os TODOs que você deixou para validar o tamanho máximo da lista) e manda salvar.
 */

/*
O java já enxerga que quem implementa a interface wishlistRepository
é o WishlistRepositoryMongoImpl (pois eu anotei aquela classe como @repository)

Então o java já injeta aqui pra mim.
E quando o controller chamar aqui, vai ser executado o conteúdo aqui
 */


/*

    ProductId::toString é uma Method Reference (referência de metodo).
    É uma forma mais curta de escrever: id -> id.toString().

 */


/*

.collect(Collectors.toSet())
O .collect() é a operação final que fecha a esteira e junta os elementos transformados
de volta em uma coleção.

Collectors.toSet() diz ao Java para agrupar esses novos textos em um Set<String>.

Detalhe importante: Uma das principais características de um Set (conjunto) em Java é que ele não permite elementos duplicados. Se a sua lista original tivesse dois IDs iguais, o Set final guardará apenas um.
 */