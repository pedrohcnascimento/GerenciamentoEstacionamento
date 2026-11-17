package inpark.gerenciamentoestacionamento.application.unit.security;

import inpark.gerenciamentoestacionamento.application.dto.requests.login.UsuarioLoginRequest;
import inpark.gerenciamentoestacionamento.application.services.AuthService;
import inpark.gerenciamentoestacionamento.dao.UsuarioDao;
import inpark.gerenciamentoestacionamento.infraestructure.security.JwtService;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Usuario;
import inpark.gerenciamentoestacionamento.model.enums.Role;
import inpark.gerenciamentoestacionamento.model.exceptions.CredenciaisInvalidasException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioDao usuarios;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwt;

    @InjectMocks
    private AuthService service;

    @Test
    void deveGerarTokenQuandoCredenciaisValidas() {
        UsuarioLoginRequest req = new UsuarioLoginRequest("rafael@senai.com", "123");
        Usuario user = DonoEstacionamento.builder()
                .email("rafael@senai.com")
                .senha("encoded")
                .role(Role.ADMIN)
                .build();

        when(usuarios.findByEmail("rafael@senai.com")).thenReturn(Optional.of(user));
        when(encoder.matches("123", "encoded")).thenReturn(true);
        when(jwt.generateToken("rafael@senai.com", "ADMIN")).thenReturn("token123");

        String token = service.login(req);

        assertEquals("token123", token);
    }

    @Test
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        UsuarioLoginRequest req = new UsuarioLoginRequest("rafael@senai.com", "123");
        Usuario user = DonoEstacionamento.builder()
                .email("rafael@senai.com")
                .senha("encoded")
                .role(Role.ADMIN)
                .build();

        when(usuarios.findByEmail("rafael@senai.com")).thenReturn(Optional.of(user));
        when(encoder.matches("123", "encoded")).thenReturn(false);

        assertThrows(CredenciaisInvalidasException.class, () -> service.login(req));
    }
}
