package inpark.gerenciamentoestacionamento.model.exceptions;

public class IdNaoCadastrado extends RuntimeException {
    public IdNaoCadastrado(String message) {
        super(message);
    }
}
