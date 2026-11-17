package inpark.gerenciamentoestacionamento.application.integration.security;

import inpark.gerenciamentoestacionamento.GerenciamentoEstacionamentoApplication;
import inpark.gerenciamentoestacionamento.dao.EstacionamentoDao;
import inpark.gerenciamentoestacionamento.dao.ClienteDao;
import inpark.gerenciamentoestacionamento.dao.DonoDao;
import inpark.gerenciamentoestacionamento.infraestructure.security.JwtService;
import inpark.gerenciamentoestacionamento.infraestructure.security.UsuarioDetailService;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import inpark.gerenciamentoestacionamento.model.enums.Role;
import inpark.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
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
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GerenciamentoEstacionamentoApplication.class)
@AutoConfigureMockMvc
class SecurityAuthorizationIntegrationTest {

    @Autowired
    private DonoDao DonoDao;

    @Autowired
    private ClienteDao ClienteDao;

    @Autowired
    private EstacionamentoDao EstacionamentoDao;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UsuarioDetailService usuarioDetailsService;

    private String token;
    private Long donoId;
    private Long clienteId;
    private Long estacionamentoId;

    @BeforeEach
    void Setup() {
        Date birthDate = Date.from(LocalDate.of(1990, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueDonoEmail = "dono" + UUID.randomUUID() + "@gmail.com";
        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Pedro Dono")
                .email(uniqueDonoEmail)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDate)
                .role(Role.DONO)
                .tipoDeUsuario(TipoDeUsuario.DONO)
                .status(true)
                .build();
        dono = DonoDao.save(dono);
        donoId = dono.getId();

        Cliente cliente = Cliente.builder()
                .nome("Cliente Teste")
                .email("cliente" + UUID.randomUUID() + "@gmail.com")
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDate)
                .role(Role.CLIENTE)
                .tipoDeUsuario(TipoDeUsuario.CLIENTE)
                .status(true)
                .build();
        cliente = ClienteDao.save(cliente);
        clienteId = cliente.getId();

        Estacionamento est = Estacionamento.builder()
                .nome("Estacionamento Teste")
                .endereco("Rua Teste, 100")
                .CEP("12345-678")
                .numero("100")
                .fotoUrl("foto.jpg")
                .numeroAlvaraDeFuncionamento("ALV-" + UUID.randomUUID().toString().substring(0, 8))
                .horaAbertura(LocalTime.of(8, 0))
                .horaFechamento(LocalTime.of(22, 0))
                .vagasPreferenciais(5)
                .maxVagas(50)
                .vagasDisponiveis(50)
                .numeroDeEscrituraImovel("ESC-" + UUID.randomUUID().toString().substring(0, 8))
                .dono(dono)
                .build();
        est = EstacionamentoDao.save(est);
        estacionamentoId = est.getId();

        token = jwtService.generateToken(dono.getEmail(), dono.getRole().name());
    }

    private void mockUsuario(String email, String role) {
        UserDetails user = User.withUsername(email)
                .password("encoded")
                .roles(role)
                .build();
        when(usuarioDetailsService.loadUserByUsername(email)).thenReturn(user);
    }

    @Test
    void adminDevePoderCriarValor() throws Exception {
        String email = "admin@senai.com";
        mockUsuario(email, "ADMIN");
        String token = jwtService.generateToken(email, "ADMIN");

        String valorJson = """
            {
              "tipoDeCobranca": "PORHORA",
              "tipoDePagamento": "DINHEIRO",
              "preco": 10.0,
              "periodo": "FINALDESEMANA",
              "estacionamentoId": %d
            }
            """.formatted(estacionamentoId);

        mockMvc.perform(post("/valor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valorJson))
                .andExpect(status().isCreated());
    }

    @Test
    void adminNaoDeveSerNegadoEmNenhumaRota() throws Exception {
        String email = "admin@senai.com";
        mockUsuario(email, "ADMIN");
        String token = jwtService.generateToken(email, "ADMIN");

        mockMvc.perform(get("/carro")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void donoDevePoderCriarEstacionamento() throws Exception {
        String email = "dono@senai.com";
        mockUsuario(email, "DONO");
        String token = jwtService.generateToken(email, "DONO");

        String estacionamentoJson = """
            {
              "nome": "Estacionamento Teste",
              "endereco": "Rua das Flores, 123",
              "CEP": "12345-678",
              "numero": "123",
              "foto": "foto.jpg",
              "numeroAlvaraDeFuncionamento": "123456789",
              "horaFechamento": "22:00:00",
              "horaAbertura": "08:00:00",
              "vagasPreferenciais": 5,
              "maximoDeVagas": 100,
              "numeroDeEscrituraImovel": "987654321"
            }
            """;

        mockMvc.perform(
                post("/estacionamento/" + donoId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estacionamentoJson)
        ).andExpect(status().isCreated());
    }

    @Test
    void donoNaoDevePoderPostarCarro() throws Exception {
        String email = "dono@senai.com";
        mockUsuario(email, "DONO");
        String token = jwtService.generateToken(email, "DONO");

        String carroJson = """
            {
              "clienteId": %d,
              "placa": "TEST-1234",
              "modelo": "TesteModelo",
              "cor": "Preto"
            }
            """.formatted(clienteId);

        mockMvc.perform(post("/carro")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carroJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void gerenteNaoDevePoderCriarEstacionamento() throws Exception {
        String email = "gerente@senai.com";
        mockUsuario(email, "GERENTE");
        String token = jwtService.generateToken(email, "GERENTE");

        String estacionamentoJson = """
            {
              "nome": "Novo",
              "endereco": "Rua X, 1",
              "CEP": "00000-000",
              "numero": "1",
              "foto": "x.jpg",
              "numeroAlvaraDeFuncionamento": "111222333",
              "horaFechamento": "22:00:00",
              "horaAbertura": "08:00:00",
              "vagasPreferenciais": 2,
              "maximoDeVagas": 20,
              "numeroDeEscrituraImovel": "000111"
            }
            """;

        mockMvc.perform(post("/estacionamento/" + donoId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estacionamentoJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void gerenteNaoDevePoderConsultarGerente() throws Exception {
        String email = "gerente@senai.com";
        mockUsuario(email, "GERENTE");
        String token = jwtService.generateToken(email, "GERENTE");

        mockMvc.perform(get("/gerente")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void clienteDevePoderCriarCarro() throws Exception {
        String email = "cliente@senai.com";
        mockUsuario(email, "CLIENTE");
        String token = jwtService.generateToken(email, "CLIENTE");
        String placa = "ABC" + System.currentTimeMillis() % 10000;

        String carroJson = """
            {
              "clienteId": %d,
              "placa": "%s",
              "modelo": "Fiesta",
              "cor": "Azul"
            }
            """.formatted(clienteId, placa);

        mockMvc.perform(post("/carro")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carroJson))
                .andExpect(status().isCreated());
    }

    @Test
    void clienteNaoDevePoderCriarValor() throws Exception {
        String email = "cliente@senai.com";
        mockUsuario(email, "CLIENTE");
        String token = jwtService.generateToken(email, "CLIENTE");

        String valorJson = """
            {
              "tipoDeCobranca": "PORHORA",
              "tipoDePagamento": "DINHEIRO",
              "preco": 15.0,
              "periodo": "FINALDESEMANA",
              "estacionamentoId": %d
            }
            """.formatted(estacionamentoId);

        mockMvc.perform(post("/valor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valorJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveNegarAcessoSemToken() throws Exception {
        mockMvc.perform(get("/carro"))
                .andExpect(status().isForbidden());
    }
}
