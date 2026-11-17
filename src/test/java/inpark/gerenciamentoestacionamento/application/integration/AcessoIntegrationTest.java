package inpark.gerenciamentoestacionamento.application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import inpark.gerenciamentoestacionamento.GerenciamentoEstacionamentoApplication;
import inpark.gerenciamentoestacionamento.application.dto.requests.AcessoRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.CarroRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.EstacionamentoRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.AcessoResponse;
import inpark.gerenciamentoestacionamento.application.dto.response.EstacionamentoResponse;
import inpark.gerenciamentoestacionamento.dao.*;
import inpark.gerenciamentoestacionamento.dao.ClienteDao;
import inpark.gerenciamentoestacionamento.dao.DonoDao;
import inpark.gerenciamentoestacionamento.dao.GerenteDao;
import inpark.gerenciamentoestacionamento.infraestructure.security.JwtService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GerenciamentoEstacionamentoApplication.class)
@AutoConfigureMockMvc
public class AcessoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

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
    private EstacionamentoDao EstacionamentoDao;
    @Autowired
    private DonoDao DonoDao;
    @Autowired
    private ClienteDao ClienteDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    private String token;
    private Long estacionamentoId;
    private Long carroId;

    @BeforeEach
    void setup() throws Exception {
        AcessoDao.deleteAll();
        AvaliacaoDao.deleteAll();
        ReservaDao.deleteAll();
        ValorDao.deleteAll();
        GerenteDao.deleteAll();
        CarroDao.deleteAll();
        EstacionamentoDao.deleteAll();
        DonoDao.deleteAll();
        ClienteDao.deleteAll();

        Date birthDate = Date.from(LocalDate.of(1990, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmail = "dono" + System.currentTimeMillis() + "@gmail.com";
        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Pedro Dono")
                .email(uniqueEmail)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDate)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.DONO)
                .status(true)
                .build();

        DonoDao.save(dono);

        token = jwtService.generateToken(dono.getEmail(), dono.getRole().name());

        var estacionamentoRequest = new EstacionamentoRequest(
                "Estacionamento Central",
                "Rua A, 123",
                "11111111",
                "283",
                "foto.jpg",
                "123456",
                LocalTime.of(8, 0),
                LocalTime.of(22, 0),
                150,
                200,
                "123456"
        );

        var responseEstacionamento = mockMvc.perform(withAuth(post("/estacionamento/" + dono.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(estacionamentoRequest))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EstacionamentoResponse estacionamentoResponse = objectMapper.readValue(responseEstacionamento, EstacionamentoResponse.class);
        estacionamentoId = estacionamentoResponse.id();

        String emailCliente = "cliente" + System.currentTimeMillis() + "@gmail.com";
        Cliente cliente = Cliente.builder()
                .nome("Cliente Teste")
                .email(emailCliente)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDate)
                .role(Role.CLIENTE)
                .tipoDeUsuario(TipoDeUsuario.CLIENTE)
                .status(true)
                .build();

        ClienteDao.save(cliente);

        var carroRequest = new CarroRequest(
                cliente.getId(),
                "ABC1234",
                "Modelo X",
                "Cor Vermelha"
        );
        mockMvc.perform(withAuth(post("/carro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(carroRequest))))
                .andExpect(status().isCreated());
    }

    private AcessoRequest acessoRequestValido() {
        return new AcessoRequest(
                1L,
                "ABC1234",
                LocalTime.parse("08:00:00"),
                LocalTime.parse("10:00:00"),
                20.0,
                estacionamentoId
        );
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + token);
    }

    @Test
    void deveCadastrarAcessoValido() throws Exception {
        mockMvc.perform(withAuth(post("/acesso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(acessoRequestValido()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placaDoCarro").value("ABC1234"))
                .andExpect(jsonPath("$.valorAPagar").value(20.0));
    }

    @Test
    void deveAtualizarAcesso() throws Exception {
        var response = mockMvc.perform(withAuth(post("/acesso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(acessoRequestValido()))))
                .andExpect(status().isCreated())
                .andReturn();

        AcessoResponse acesso = objectMapper.readValue(response.getResponse().getContentAsString(), AcessoResponse.class);

        var atualizado = new AcessoRequest(
                carroId,
                "ABC1234",
                LocalTime.parse("09:00:00"),
                LocalTime.parse("11:00:00"),
                25.0,
                estacionamentoId
        );

        mockMvc.perform(withAuth(put("/acesso/" + acesso.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(atualizado))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placaDoCarro").value("ABC1234"))
                .andExpect(jsonPath("$.valorAPagar").value(25.0));
    }

    @Test
    void deveDeletarAcesso() throws Exception {
        var response = mockMvc.perform(withAuth(post("/acesso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(acessoRequestValido()))))
                .andExpect(status().isCreated())
                .andReturn();

        AcessoResponse acesso = objectMapper.readValue(response.getResponse().getContentAsString(), AcessoResponse.class);

        mockMvc.perform(withAuth(delete("/acesso/" + acesso.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarAcessoPorId() throws Exception {
        var response = mockMvc.perform(withAuth(post("/acesso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(acessoRequestValido()))))
                .andExpect(status().isCreated())
                .andReturn();

        AcessoResponse acesso = objectMapper.readValue(response.getResponse().getContentAsString(), AcessoResponse.class);

        mockMvc.perform(withAuth(get("/acesso/" + acesso.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placaDoCarro").value("ABC1234"))
                .andExpect(jsonPath("$.valorAPagar").value(20.0));
    }

    @Test
    void deveListarAcessos() throws Exception {
        mockMvc.perform(withAuth(post("/acesso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(acessoRequestValido()))))
                .andExpect(status().isCreated());

        mockMvc.perform(withAuth(get("/acesso")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].placaDoCarro").value("ABC1234"));
    }

    @Test
    void deveRetornarErroAoBuscarAcessoInexistente() throws Exception {
        mockMvc.perform(withAuth(get("/acesso/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("ID buscado não foi encontrado no sistema!"));
    }

    @Test
    void deveRetornarErroAoDeletarAcessoInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/acesso/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Acesso buscado não cadastrado no sistema"));
    }
}
