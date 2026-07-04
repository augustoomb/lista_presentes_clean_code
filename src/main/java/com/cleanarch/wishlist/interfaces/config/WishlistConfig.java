package com.cleanarch.wishlist.interfaces.config;

import com.cleanarch.wishlist.application.config.WishlistPropertiesProvider;
import com.cleanarch.wishlist.application.usecase.WishlistUseCase;
import com.cleanarch.wishlist.domain.repository.WishlistRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WishlistConfig {

    @Bean // Diz ao Spring: "O objeto retornado por este metodo deve se tornar um Bean (ser gerenciado por você)"
    public WishlistUseCase wishlistUseCase(WishlistRepository wishlistRepository, WishlistPropertiesProvider wishlistPropertiesProvider) {

        // Aqui você cria o objeto manualmente usando Java puro,
        // mas deixa o Spring resolver a interface do repositório que ele já conhece.
        return new WishlistUseCase(wishlistRepository, wishlistPropertiesProvider);
    }
}

/*
Porque essa classe de configuration???

Para entender o porquê da existência da classe WishlistConfig,
precisamos olhar para a classe WishlistUseCase que você enviou na primeira mensagem.

Repare em um detalhe crucial: o seu WishlistUseCase não possui nenhuma anotação
 do Spring (ele não tem @Service, não tem @Component, nada). É uma classe Java pura.


 O Problema: O Spring não enxerga o seu UseCase
No seu WishlistController, você usou a anotação @Autowired (ou injeção via construtor)
para pedir ao Spring: "Ei, Spring, me dá uma instância de WishlistUseCase".

Porém, se você não colocar nenhuma anotação do Spring no WishlistUseCase
(para manter o domínio limpo de frameworks), o Spring fará o escaneamento do
projeto e pensará: "Eu não sei o que é um WishlistUseCase.
Ninguém me disse como criar esse objeto!".
O resultado seria um erro clássico de inicialização (NoSuchBeanDefinitionException).


A Solução: A classe WishlistConfig serve como o "Manual de Instruções"
Como você não quis (corretamente) "sujar" seu caso de uso com anotações do framework,
você criou a classe WishlistConfig na camada de interfaces/infraestrutura para
ensinar o Spring a criar aquela classe pura.
 */


/*
O fluxo que o Spring faz nos bastidores:
1-O Spring inicia e lê a classe WishlistConfig por causa do @Configuration.

2-Ele vê o metodo anotado com @Bean que promete retornar um WishlistUseCase.

3-O Spring nota: "Hum, para eu executar esse metodo e criar o caso de uso,
eu preciso de um WishlistRepository como argumento".

4-O Spring olha na memória dele, encontra o WishlistRepositoryMongoImpl
(que tem o @Repository), e o passa como argumento para o metodo.

5-O metodo executa o new WishlistUseCase(wishlistRepository)
e entrega o objeto pronto para o Spring.

6-Agora sim: Quando o WishlistController pede o @Autowired do WishlistUseCase,
o Spring já tem essa instância guardada na memória dele e faz a entrega com sucesso.

Em resumo: Por que não usar logo o @Service no UseCase?
Você poderia simplesmente colocar @Service em cima da classe WishlistUseCase
e apagar essa classe de configuração? Sim, funcionaria perfeitamente para o Spring.

Mas você quebraria a regra de ouro da Clean Architecture: a sua camada de aplicação (application.usecase) passaria a depender diretamente do pacote org.springframework. Se um dia você mudasse de framework (para o Quarkus, Micronaut ou fizesse uma biblioteca Java pura), seu caso de uso estaria acoplado ao Spring.

A classe @Configuration funciona como uma ponte de fronteira: o seu caso de uso continua sendo um Java puríssimo e isolado do mundo, e a infraestrutura do Spring se encarrega de adotá-lo e gerenciá-lo externamente.
 */