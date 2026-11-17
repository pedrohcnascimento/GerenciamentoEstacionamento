package parkwise.gerenciamentoestacionamento.application.services;

import parkwise.gerenciamentoestacionamento.application.dto.requests.login.UsuarioLoginRequest;
import parkwise.gerenciamentoestacionamento.dao.UsuarioDao;
import parkwise.gerenciamentoestacionamento.infraestructure.security.JwtService;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.Usuario;
import parkwise.gerenciamentoestacionamento.model.exceptions.CredenciaisInvalidasException;
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
