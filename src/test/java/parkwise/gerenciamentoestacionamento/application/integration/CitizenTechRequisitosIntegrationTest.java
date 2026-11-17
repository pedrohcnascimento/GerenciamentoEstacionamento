package parkwise.gerenciamentoestacionamento.application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import parkwise.gerenciamentoestacionamento.GerenciamentoEstacionamentoApplication;
import parkwise.gerenciamentoestacionamento.application.dto.requests.CarroRequest;
import parkwise.gerenciamentoestacionamento.application.dto.requests.EstacionamentoRequest;
import parkwise.gerenciamentoestacionamento.application.dto.requests.usuario.ClienteRequest;
import parkwise.gerenciamentoestacionamento.dao.*;
import parkwise.gerenciamentoestacionamento.infraestructure.security.JwtService;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import parkwise.gerenciamentoestacionamento.model.enums.Role;
import parkwise.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = GerenciamentoEstacionamentoApplication.class)
@AutoConfigureMockMvc
public class CitizenTechRequisitosIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired private AcessoDao acessoDao;
    @Autowired private AvaliacaoDao avaliacaoDao;
    @Autowired private ReservaDao reservaDao;
    @Autowired private ValorDao valorDao;
    @Autowired private GerenteDao gerenteDao;
    @Autowired private CarroDao carroDao;
    @Autowired private EstacionamentoDao estacionamentoDao;
    @Autowired private ClienteDao clienteDao;
    @Autowired private DonoDao donoDao;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    private Long clienteId;
    private Long donoId;
    private String clienteToken;
    private String donoToken;
    private String clienteEmail;

    @BeforeEach
    void setup() {
        acessoDao.deleteAll();
        avaliacaoDao.deleteAll();
        reservaDao.deleteAll();
        valorDao.deleteAll();
        gerenteDao.deleteAll();
        carroDao.deleteAll();
        estacionamentoDao.deleteAll();
        clienteDao.deleteAll();
        donoDao.deleteAll();

        Date nascimento = Date.from(LocalDate.of(1995, 5, 10).atStartOfDay(ZoneId.systemDefault()).toInstant());
        clienteEmail = "cidadao" + System.currentTimeMillis() + "@parkwise.com";

        Cliente cliente = Cliente.builder()
                .nome("Cidadao Teste")
                .email(clienteEmail)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(nascimento)
                .role(Role.CLIENTE)
                .tipoDeUsuario(TipoDeUsuario.CLIENTE)
                .status(true)
                .build();
        clienteDao.save(cliente);
        clienteId = cliente.getId();
        clienteToken = jwtService.generateToken(cliente.getEmail(), cliente.getRole().name());

        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Dono Teste")
                .email("dono" + System.currentTimeMillis() + "@parkwise.com")
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(nascimento)
                .role(Role.DONO)
                .tipoDeUsuario(TipoDeUsuario.DONO)
                .status(true)
                .build();
        donoDao.save(dono);
        donoId = dono.getId();
        donoToken = jwtService.generateToken(dono.getEmail(), dono.getRole().name());
    }

    @Test
    void deveRetornar400QuandoBeanValidationFalhar() throws Exception {
        ClienteRequest invalido = new ClienteRequest("", "email-invalido", "123", null);

        mockMvc.perform(post("/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nome").exists())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    void deveRetornar409QuandoEmailDuplicadoNoCadastro() throws Exception {
        Date nascimento = Date.from(LocalDate.of(1995, 5, 10).atStartOfDay(ZoneId.systemDefault()).toInstant());
        ClienteRequest duplicado = new ClienteRequest("Outro Nome", clienteEmail, "123456", nascimento);

        mockMvc.perform(post("/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicado)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void deveCriarEListarRotasAninhadas1NComLocationHeader() throws Exception {
        CarroRequest carroReq = new CarroRequest(clienteId, "ABC1D23", "Onix", "Branco");

        mockMvc.perform(post("/cliente/" + clienteId + "/carros")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carroReq)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/carros/")))
                .andExpect(jsonPath("$.placa").value("ABC1D23"));

        mockMvc.perform(get("/cliente/" + clienteId + "/carros")
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].placa").value("ABC1D23"));

        EstacionamentoRequest estReq = new EstacionamentoRequest(
                "Estacionamento Central",
                "Av Paulista",
                "01310100",
                "1000",
                null,
                "ALV-2026-01",
                LocalTime.of(22, 0),
                LocalTime.of(6, 0),
                5,
                50,
                "ESC-999"
        );

        mockMvc.perform(post("/dono/" + donoId + "/estacionamentos")
                        .header("Authorization", "Bearer " + donoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(estReq)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/estacionamentos/")))
                .andExpect(jsonPath("$.nome").value("Estacionamento Central"));

        mockMvc.perform(get("/dono/" + donoId + "/estacionamentos")
                        .header("Authorization", "Bearer " + donoToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Estacionamento Central"));
    }

    @Test
    void deveRetornar409QuandoPlacaDuplicada() throws Exception {
        CarroRequest carroReq = new CarroRequest(clienteId, "XYZ9K88", "Polo", "Prata");

        mockMvc.perform(post("/cliente/" + clienteId + "/carros")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carroReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(clienteId));

        mockMvc.perform(post("/cliente/" + clienteId + "/carros")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carroReq)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void deveLiberarCorsNasRespostas() throws Exception {
        mockMvc.perform(options("/estacionamento")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }
}
