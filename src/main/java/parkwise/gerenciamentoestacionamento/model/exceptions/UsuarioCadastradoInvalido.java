package parkwise.gerenciamentoestacionamento.model.exceptions;

public class UsuarioCadastradoInvalido extends RuntimeException {
    public UsuarioCadastradoInvalido(String message) {
        super(message);
    }
}
