package parkwise.gerenciamentoestacionamento.application.integration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import parkwise.gerenciamentoestacionamento.GerenciamentoEstacionamentoApplication;
import parkwise.gerenciamentoestacionamento.application.dto.requests.login.UsuarioLoginRequest;
import parkwise.gerenciamentoestacionamento.dao.*;
import parkwise.gerenciamentoestacionamento.dao.GerenteDao;
import parkwise.gerenciamentoestacionamento.dao.UsuarioDao;
import parkwise.gerenciamentoestacionamento.infraestructure.security.JwtService;
import parkwise.gerenciamentoestacionamento.infraestructure.security.UsuarioDetailService;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import parkwise.gerenciamentoestacionamento.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GerenciamentoEstacionamentoApplication.class)
@AutoConfigureMockMvc
class JwtAuthenticationFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    @Autowired
    private AcessoDao AcessoDao;
    @Autowired
    private AvaliacaoDao AvaliacaoDao;
    @Autowired
    private ReservaDao ReservaDao;
    @Autowired
    private ValorDao ValorDao;
    @Autowired
    private GerenteDao GerenteDao;
    @Autowired
    private CarroDao CarroDao;
    @Autowired
    private UsuarioDao usuarios;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private EstacionamentoDao EstacionamentoDao;

    @MockitoBean
    private UsuarioDetailService usuarioDetailService;

    @BeforeEach
    void setup() {
        AcessoDao.deleteAll();
        AvaliacaoDao.deleteAll();
        ReservaDao.deleteAll();
        ValorDao.deleteAll();
        GerenteDao.deleteAll();
        CarroDao.deleteAll();
        EstacionamentoDao.deleteAll();
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
    void deveRetornar405QuandoMetodoHttpNaoSuportado() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void devePermitirAcessoAoLoginSemTokenMesmoComSenhaInvalida() throws Exception {
        UsuarioLoginRequest req = new UsuarioLoginRequest("rafael@senai.com", "senhaErrada");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveNegarAcessoAEndpointProtegidoSemToken() throws Exception {
        mockMvc.perform(get("/gerente"))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirAcessoComTokenValido() throws Exception {
        String email = "rafael@senai.com";
        String token = jwtService.generateToken(email, "ADMIN");

        UserDetails mockUser = User.withUsername(email)
                .password("encoded")
                .roles("ADMIN")
                .build();

        when(usuarioDetailService.loadUserByUsername(email)).thenReturn(mockUser);

        mockMvc.perform(get("/estacionamento")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
