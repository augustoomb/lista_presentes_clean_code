package com.cleanarch.wishlist.interfaces.handler;

import com.cleanarch.wishlist.domain.exception.BusinessException;
import com.cleanarch.wishlist.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice // ESSA CLASSE SERÁ RESPONSÁVEL PELAS EXCEÇÕES LANÇADAS EM QUALQUER CONTROLLER REST DA APLICAÇÃO
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String>handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String>handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Argumento inválido: " + ex.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O parâmetro requirido não está no path.");
    }

    // PARA @ExceptionHandler(NoHandlerFoundException.class) FUNCIONAR É NECESSÁRIO CONFIGURAR O ARQUIVO
    // application.properties, com o seguinte:
        /*
            spring.mvc.throw-exception-if-no-handler-found=true
            spring.web.resources.add-mappings=false
         */
    // o que isso melhora?
        /*
        Por ex: se eu tentar fazer uma requisição para: http://localhost:8080/api/wishlists//products
        vai cair na exceção que eu criei acima (NoHandlerFoundException).
        Sem ela + conf no arquivo application.properties, um erro estranho seria mostrado na resposta da req
         */
}
