package parkwise.gerenciamentoestacionamento.model.exceptions;

public class ConflitoDeRegraDeNegocioException extends RuntimeException {
    public ConflitoDeRegraDeNegocioException(String message) {
        super(message);
    }
}
