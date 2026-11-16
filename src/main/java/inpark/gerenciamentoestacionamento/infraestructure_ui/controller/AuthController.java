package inpark.gerenciamentoestacionamento.infraestructure_ui.controller;

import inpark.gerenciamentoestacionamento.application.dto.requests.login.UsuarioLoginRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.login.UsuarioLoginResponse;
import inpark.gerenciamentoestacionamento.application.mappers.usuario.ClienteMapper;
import inpark.gerenciamentoestacionamento.application.mappers.usuario.DonoMapper;
import inpark.gerenciamentoestacionamento.application.mappers.usuario.GerenteMapper;
import inpark.gerenciamentoestacionamento.application.services.AuthService;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.usuario.ClienteRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.usuario.DonoRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.usuario.GerenteRepository;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import inpark.gerenciamentoestacionamento.model.enums.Role;
import inpark.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import inpark.gerenciamentoestacionamento.model.exceptions.TipoDeUsuarioInvalido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final ClienteRepository clienteRepository;
    private final GerenteRepository gerenteRepository;
    private final DonoRepository donoRepository;

    @PostMapping("/login")
    @Operation(
            method = "POST",
            summary = "Realizar login",
            description = "Retorna um token JWT caso o login seja válido",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciais de acesso do usuário",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UsuarioLoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de Login",
                                    value = """
                                        {
                                          "email": "admin@teste.com",
                                          "senha": "123456"
                                        }
                                        """
                            )
                    )
            )
    )
    public ResponseEntity<UsuarioLoginResponse> login(@RequestBody UsuarioLoginRequest dto) {
        String token = authService.login(dto);
        return ResponseEntity.ok(new UsuarioLoginResponse(token));
    }

    @PostMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(401).body(java.util.Map.of(
                    "erro", "Usuário não autenticado. Faça login em /auth/login e informe o token JWT no botão Authorize."
            ));
        }

        String username = userDetails.getUsername();

        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + TipoDeUsuario.CLIENTE.name()))) {
            return ResponseEntity.ok().body(ClienteMapper.fromEntity(clienteRepository.findByEmail(username).get()));
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + TipoDeUsuario.DONO.name()))) {
            return ResponseEntity.ok().body(DonoMapper.fromEntity(donoRepository.findByEmail(username).get()));
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + TipoDeUsuario.GERENTE.name()))) {
            return ResponseEntity.ok().body(GerenteMapper.fromEntity(gerenteRepository.findByEmail(username).get()));
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + Role.ADMIN.name()))) {
            var clienteOpt = clienteRepository.findByEmail(username);
            if (clienteOpt.isPresent()) {
                return ResponseEntity.ok().body(ClienteMapper.fromEntity(clienteOpt.get()));
            }
            var donoOpt = donoRepository.findByEmail(username);
            if (donoOpt.isPresent()) {
                return ResponseEntity.ok().body(DonoMapper.fromEntity(donoOpt.get()));
            }
            var gerenteOpt = gerenteRepository.findByEmail(username);
            if (gerenteOpt.isPresent()) {
                return ResponseEntity.ok().body(GerenteMapper.fromEntity(gerenteOpt.get()));
            }
        }
        throw new TipoDeUsuarioInvalido("Tipo de usuario não encontrado no sistema!");
    }

    @PostMapping("/criar-admin")
    public String criarAdminTeste() {
        if (clienteRepository.findByEmail("admin@teste.com").isPresent()) {
            return "Admin já existe!";
        }
        Cliente u = new Cliente();
        u.setNome("ADMIN");
        u.setDataNascimento(new Date());
        u.setEmail("admin@teste.com");
        u.setSenha(passwordEncoder.encode("123456"));
        u.setRole(Role.ADMIN);
        u.setTipoDeUsuario(TipoDeUsuario.CLIENTE);
        u.setStatus(true);
        clienteRepository.save(u);
        return "Admin criado!";
    }
}
