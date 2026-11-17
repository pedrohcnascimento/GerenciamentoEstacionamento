package inpark.gerenciamentoestacionamento.application.integration.usuarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import inpark.gerenciamentoestacionamento.GerenciamentoEstacionamentoApplication;
import inpark.gerenciamentoestacionamento.application.dto.requests.AcessoRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.CarroRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.EstacionamentoRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.usuario.DonoRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.CarroResponse;
import inpark.gerenciamentoestacionamento.application.dto.response.EstacionamentoResponse;
import inpark.gerenciamentoestacionamento.application.dto.response.usuario.DonoResponse;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GerenciamentoEstacionamentoApplication.class)
@AutoConfigureMockMvc
public class DonoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private DonoDao DonoDao;
    @Autowired
    private EstacionamentoDao EstacionamentoDao;
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
    private ClienteDao ClienteDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    private String token;
    private Long estacionamentoId;
    private Long carroId;
    private Cliente testClient;

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

        String uniqueDonoEmail = "dono" + System.currentTimeMillis() + "@gmail.com";
        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Pedro Dono")
                .email(uniqueDonoEmail)
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

        String responseEstacionamentoString = mockMvc.perform(withAuth(post("/estacionamento/" + dono.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(estacionamentoRequest))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EstacionamentoResponse estacionamentoResponse = objectMapper.readValue(responseEstacionamentoString, EstacionamentoResponse.class);
        this.estacionamentoId = estacionamentoResponse.id();

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
        testClient = ClienteDao.save(cliente);

        var carroRequest = new CarroRequest(
                testClient.getId(),
                "ABC1234",
                "Modelo X",
                "Cor Vermelha"
        );
        String responseCarroString = mockMvc.perform(withAuth(post("/carro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(carroRequest))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CarroResponse carroResponse = objectMapper.readValue(responseCarroString, CarroResponse.class);
        this.carroId = carroResponse.id();
    }

    private AcessoRequest acessoRequestValido() {
        return new AcessoRequest(
                this.carroId,
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
    void deveCadastrarDonoValido() throws Exception {
        String uniqueEmail = "dono.novo." + UUID.randomUUID() + "@gmail.com";
        DonoRequest novoDonoRequest = new DonoRequest(
                "Novo Dono Teste",
                uniqueEmail,
                "senha123",
                new Date()
        );

        mockMvc.perform(withAuth(post("/dono")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(novoDonoRequest))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value(novoDonoRequest.nome()))
                .andExpect(jsonPath("$.email").value(novoDonoRequest.email()))
                .andExpect(jsonPath("$.dataNascimento").exists());
    }

    @Test
    void deveAtualizarDono() throws Exception {
        var novoDonoRequest = new DonoRequest(
                "Dono para Atualizar",
                "dono.update" + System.currentTimeMillis() + "@gmail.com",
                "senha123",
                new Date()
        );

        var savedResponse = mockMvc.perform(withAuth(post("/dono")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoDonoRequest))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var donoSalvo = objectMapper.readValue(savedResponse, DonoResponse.class);

        var atualizado = new DonoRequest(
                "Carlos Atualizado",
                "carlos.atualizado@gmail.com",
                "654321",
                new Date()
        );

        mockMvc.perform(withAuth(put("/dono/" + donoSalvo.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Atualizado"));
    }

    @Test
    void deveDeletarDono() throws Exception {
        var novoDonoRequest = new DonoRequest(
                "Dono para Deletar",
                "dono.delete" + System.currentTimeMillis() + "@gmail.com",
                "senha123",
                new Date()
        );

        var savedResponse = mockMvc.perform(withAuth(post("/dono")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoDonoRequest))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var donoSalvo = objectMapper.readValue(savedResponse, DonoResponse.class);

        mockMvc.perform(withAuth(delete("/dono/" + donoSalvo.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarErroAoDeletarDonoInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/dono/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Dono buscado não cadastrado no sistema"));
    }
}
