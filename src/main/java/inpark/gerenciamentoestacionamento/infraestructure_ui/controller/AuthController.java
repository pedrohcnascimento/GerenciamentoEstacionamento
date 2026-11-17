package inpark.gerenciamentoestacionamento.infraestructure_ui.controller;

import inpark.gerenciamentoestacionamento.application.dto.requests.login.UsuarioLoginRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.login.UsuarioLoginResponse;
import inpark.gerenciamentoestacionamento.application.mappers.usuario.ClienteMapper;
import inpark.gerenciamentoestacionamento.application.mappers.usuario.DonoMapper;
import inpark.gerenciamentoestacionamento.application.mappers.usuario.GerenteMapper;
import inpark.gerenciamentoestacionamento.application.services.AuthService;
import inpark.gerenciamentoestacionamento.dao.ClienteDao;
import inpark.gerenciamentoestacionamento.dao.DonoDao;
import inpark.gerenciamentoestacionamento.dao.GerenteDao;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import inpark.gerenciamentoestacionamento.model.enums.Role;
import inpark.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import inpark.gerenciamentoestacionamento.model.exceptions.TipoDeUsuarioInvalido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(
        name = "1. Autenticação",
        description = "Login JWT, consulta do perfil autenticado e criação de usuário ADMIN para testes"
)
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final ClienteDao ClienteDao;
    private final GerenteDao GerenteDao;
    private final DonoDao DonoDao;

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(
            summary = "Realizar login",
            description = "Acesso público. Valida e-mail e senha e retorna um token JWT Bearer.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
            },
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
    public ResponseEntity<UsuarioLoginResponse> login(@RequestBody @Valid UsuarioLoginRequest dto) {
        String token = authService.login(dto);
        return ResponseEntity.ok(new UsuarioLoginResponse(token));
    }

    @PostMapping("/me")
    @Operation(
            summary = "Consultar dados do usuário autenticado",
            description = "Requer token JWT válido. Retorna os dados do usuário correspondente ao token informado no botão Authorize.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dados do usuário retornados com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            }
    )
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(401).body(java.util.Map.of(
                    "erro", "Usuário não autenticado. Faça login em /auth/login e informe o token JWT no botão Authorize."
            ));
        }

        String username = userDetails.getUsername();

        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + TipoDeUsuario.CLIENTE.name()))) {
            return ResponseEntity.ok().body(ClienteMapper.fromEntity(ClienteDao.findByEmail(username).get()));
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + TipoDeUsuario.DONO.name()))) {
            return ResponseEntity.ok().body(DonoMapper.fromEntity(DonoDao.findByEmail(username).get()));
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + TipoDeUsuario.GERENTE.name()))) {
            return ResponseEntity.ok().body(GerenteMapper.fromEntity(GerenteDao.findByEmail(username).get()));
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + Role.ADMIN.name()))) {
            var clienteOpt = ClienteDao.findByEmail(username);
            if (clienteOpt.isPresent()) {
                return ResponseEntity.ok().body(ClienteMapper.fromEntity(clienteOpt.get()));
            }
            var donoOpt = DonoDao.findByEmail(username);
            if (donoOpt.isPresent()) {
                return ResponseEntity.ok().body(DonoMapper.fromEntity(donoOpt.get()));
            }
            var gerenteOpt = GerenteDao.findByEmail(username);
            if (gerenteOpt.isPresent()) {
                return ResponseEntity.ok().body(GerenteMapper.fromEntity(gerenteOpt.get()));
            }
        }
        throw new TipoDeUsuarioInvalido("Tipo de usuario não encontrado no sistema!");
    }

    @PostMapping("/criar-admin")
    @SecurityRequirements
    @Operation(
            summary = "Criar usuário ADMIN de teste",
            description = "Acesso público (desenvolvimento). Cria o usuário admin@teste.com (senha 123456) com perfil ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Admin criado ou já existente")
            }
    )
    public String criarAdminTeste() {
        if (ClienteDao.findByEmail("admin@teste.com").isPresent()) {
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
        ClienteDao.save(u);
        return "Admin criado!";
    }
}
