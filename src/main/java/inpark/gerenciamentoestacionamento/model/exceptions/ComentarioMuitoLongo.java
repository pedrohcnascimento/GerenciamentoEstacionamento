package inpark.gerenciamentoestacionamento.model.exceptions;

public class ComentarioMuitoLongo extends RuntimeException {
    public ComentarioMuitoLongo(String message) {
        super(message);
    }
}
