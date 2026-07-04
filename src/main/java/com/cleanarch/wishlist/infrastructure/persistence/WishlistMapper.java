package com.cleanarch.wishlist.infrastructure.persistence;

import com.cleanarch.wishlist.domain.entity.Wishlist;
import com.cleanarch.wishlist.domain.vo.ProductId;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    /*
    Esta linha cria uma constante estática usando o padrão Factory do MapStruct.
    Ela serve para os cenários onde você quer usar o Mapper em um código Java puro,
    sem depender do Spring para injetá-lo (por exemplo, em testes unitários rápidos).
     Você poderia chamá-lo direto fazendo WishlistMapper.INSTANCE.toDomain(doc).
     */
    WishlistMapper INSTANCE = Mappers.getMapper(WishlistMapper.class);


    /*
    Não é necessário escrever o metodo. Apenas com a assinatura, o java já entende.
    ele vê que entra Wishlist (Domínio) e sai WishlistDocument (Banco).
    Ele vai ler os atributos de Wishlist (como id e customerId) e
    gerar um código que faz o mapeamento automático para os campos
    correspondentes do WishlistDocument
     */
    WishlistDocument toDocument(Wishlist wishlist);

    // faz exatamente o inverso do acima
    Wishlist toDomain(WishlistDocument document);


    /*
    Mas há um problema que o MapStruct encontra aqui: a Wishlist usa Set<ProductId>,
    enquanto o WishlistDocument usa Set<String>.
    O MapStruct não sabe converter essas duas coleções de tipos diferentes sozinho.
    É por isso que existem as próximas linhas.

    O MapStruct é inteligente: se ele precisa converter um Set<ProductId> em Set<String>,
    ele procura na própria interface se há algum metodo que aceite Set<ProductId>
    e retorne Set<String>. Ele acha esse metodo default.
     */

    // Métodos auxiliares para conversão de Set<ProductId> <-> Set<String>
    /*
    value.stream(): Transforma o Set<ProductId> em um fluxo de dados (Stream) para podermos processar elemento por elemento.
     .map(ProductId::toString): Para cada item do tipo ProductId dentro do fluxo, ele chama o metodo .toString() (que você implementou lá no seu Record para retornar apenas a String do ID do produto). Ou seja, ele extrai o texto de dentro do objeto.
     .collect(Collectors.toSet()): Pega todas essas Strings que foram extraídas e as agrupa de volta em um novo Set<String>
     */
    default Set<String> map(Set<ProductId> value) {
        if (value == null) return null;
        return value.stream().map(ProductId::toString).collect(Collectors.toSet());
    }


    /*
    Este faz o caminho inverso quando os dados estão vindo do MongoDB para a sua aplicação.
     value.stream(): Cria um fluxo com as String que vieram do banco de dados.
     .map(ProductId::new): Aqui está um detalhe elegante do Java. Isto é um Constructor Reference. Para cada String (ID do produto) vinda do banco, o Java faz um new ProductId(string). Lembra que o seu Record ProductId tem uma validação no construtor para não aceitar texto em branco? Ela é executada bem aqui, garantindo que nenhum dado corrompido do banco entre no seu domínio.
     .collect(Collectors.toSet()): Junta todos os novos objetos ProductId criados e entrega um Set<ProductId> prontinho para a sua Entidade.
     */
    default Set<ProductId> mapToProductId(Set<String> value) {
        if (value == null) return null;
        return value.stream().map(ProductId::new).collect(Collectors.toSet());
    }
}

/*
    Como temos duas representações da lista
    (a de negócio Wishlist e a de banco WishlistDocument), precisamos de um tradutor.
    O MapStruct faz esse papel de converter de um para o outro.
 */


/*
Resumo:
    Você apenas declara o que quer converter no topo,
     e escreve as regras de conversão customizadas (métodos default) na parte de baixo
     para os tipos complexos. O MapStruct junta tudo isso e cria a classe concreta de
      forma automatizada por baixo dos panos.
 */