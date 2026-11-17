package inpark.gerenciamentoestacionamento.application.services;

import inpark.gerenciamentoestacionamento.application.dto.requests.login.UsuarioLoginRequest;
import inpark.gerenciamentoestacionamento.dao.UsuarioDao;
import inpark.gerenciamentoestacionamento.infraestructure.security.JwtService;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Usuario;
import inpark.gerenciamentoestacionamento.model.exceptions.CredenciaisInvalidasException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioDao usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public String login(UsuarioLoginRequest req) {
        Usuario usuario = usuarios.findByEmail(req.email())
                .orElseThrow(() -> new CredenciaisInvalidasException("Usuário não encontrado"));

        if (!encoder.matches(req.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Credenciais inválidas");
        }

        return jwt.generateToken(usuario.getEmail(), usuario.getRole().name());
    }
}
