package parkwise.gerenciamentoestacionamento.infraestructure.handler;

import parkwise.gerenciamentoestacionamento.model.exceptions.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IdNaoCadastrado.class)
    public ResponseEntity<?> handleIdNaoEncontrado(IdNaoCadastrado ex) {
        return ResponseEntity.status(404).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(ConflitoDeRegraDeNegocioException.class)
    public ResponseEntity<?> handleConflitoDeRegraDeNegocio(ConflitoDeRegraDeNegocioException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("erro", "Conflito de integridade: registro duplicado ou vinculado a outros dados."));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("erro", "Acesso negado"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(400).body(Map.of("erro", ex.getMessage() != null ? ex.getMessage() : "Argumento inválido"));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        return ResponseEntity.status(400).body(Map.of("erro", "Dados obrigatórios ausentes ou inválidos."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> erros.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.status(400).body(erros);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleJsonParseError(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "status", 400,
                        "erro", "Erro ao processar JSON. Verifique o formato e os tipos dos dados enviados."
                ));
    }

    @ExceptionHandler(AvaliacaoInvalida.class)
    public ResponseEntity<?> handleMultiplasAvaliacoesIguaisException(AvaliacaoInvalida ex) {
        return ResponseEntity.status(400).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(TipoDeUsuarioInvalido.class)
    public ResponseEntity<?> handleTipoDeUsuarioInvalidoException(TipoDeUsuarioInvalido ex) {
        return ResponseEntity.status(400).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(ComentarioMuitoLongo.class)
    public ResponseEntity<?> handleComentarioMuitoLongoException(ComentarioMuitoLongo ex) {
        return ResponseEntity.status(400).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(TempoLimiteDeAvaliacaoExpedido.class)
    public ResponseEntity<?> handleTempoLimiteDeAvaliacaoExpedido(TempoLimiteDeAvaliacaoExpedido ex) {
        return ResponseEntity.status(400).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<?> handleCredencialInvalidasException(CredenciaisInvalidasException ex) {
        return ResponseEntity.status(401).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of("erro", "Método HTTP '" + ex.getMethod() + "' não é suportado para esta rota."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAnyException(Exception ex) {
        return ResponseEntity.status(500).body(Map.of("erro", "Erro interno inesperado no servidor."));
    }
}
