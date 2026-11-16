package inpark.gerenciamentoestacionamento.application.integration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import inpark.gerenciamentoestacionamento.GerenciamentoEstacionamentoApplication;
import inpark.gerenciamentoestacionamento.application.dto.requests.login.UsuarioLoginRequest;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.*;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.usuario.GerenteRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.usuario.UsuarioRepository;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import inpark.gerenciamentoestacionamento.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = GerenciamentoEstacionamentoApplication.class)
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    @Autowired
    private AcessoRepository acessoRepository;
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private ValorRepository valorRepository;
    @Autowired
    private GerenteRepository gerenteRepository;
    @Autowired
    private CarroRepository carroRepository;
    @Autowired
    private UsuarioRepository usuarios;
    @Autowired
    private EstacionamentoRepository estacionamentoRepository;
    @Autowired
    private PasswordEncoder encoder;

    @BeforeEach
    void setup() {
        acessoRepository.deleteAll();
        avaliacaoRepository.deleteAll();
        reservaRepository.deleteAll();
        valorRepository.deleteAll();
        gerenteRepository.deleteAll();
        carroRepository.deleteAll();
        estacionamentoRepository.deleteAll();
        usuarios.deleteAll();
        LocalDate localDate = LocalDate.of(1990, 1, 1);
        Date dataNascimento = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        usuarios.save(DonoEstacionamento.builder()
                .nome("Rafael")
                .dataNascimento(dataNascimento)
                .email("rafael@senai.com")
                .senha(encoder.encode("123456"))
                .role(Role.ADMIN)
                .build());
    }

    @Test
    void deveFazerLoginERetornarToken() throws Exception {
        UsuarioLoginRequest req = new UsuarioLoginRequest("rafael@senai.com", "123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void deveNegarLoginComSenhaInvalida() throws Exception {
        UsuarioLoginRequest req = new UsuarioLoginRequest("rafael@senai.com", "errada");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
