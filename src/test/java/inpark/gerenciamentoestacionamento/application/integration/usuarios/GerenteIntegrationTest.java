package inpark.gerenciamentoestacionamento.application.integration.usuarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import inpark.gerenciamentoestacionamento.GerenciamentoEstacionamentoApplication;
import inpark.gerenciamentoestacionamento.application.dto.requests.EstacionamentoRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.usuario.GerenteRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.usuario.GerenteResponse;
import inpark.gerenciamentoestacionamento.application.services.EstacionamentoService;
import inpark.gerenciamentoestacionamento.dao.*;
import inpark.gerenciamentoestacionamento.dao.DonoDao;
import inpark.gerenciamentoestacionamento.dao.GerenteDao;
import inpark.gerenciamentoestacionamento.infraestructure.security.JwtService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = GerenciamentoEstacionamentoApplication.class)
@AutoConfigureMockMvc
public class GerenteIntegrationTest {

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
    private DonoDao DonoDao;
    @Autowired
    private EstacionamentoDao EstacionamentoDao;
    @Autowired
    private EstacionamentoService estacionamentoService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    private String token;
    private Long estacionamentoId;
    private GerenteRequest testGerenteRequest;

    @BeforeEach
    void setup() {
        AcessoDao.deleteAll();
        AvaliacaoDao.deleteAll();
        ReservaDao.deleteAll();
        ValorDao.deleteAll();
        GerenteDao.deleteAll();
        EstacionamentoDao.deleteAll();
        DonoDao.deleteAll();

        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Dono Teste")
                .email("dono" + System.currentTimeMillis() + "@gmail.com")
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(new Date())
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.DONO)
                .status(true)
                .build();

        dono = DonoDao.save(dono);

        var estacionamentoDto = new EstacionamentoRequest(
                "EstacioPlay",
                "Rua das Flores",
                "12345-678",
                "123",
                null,
                "123456789",
                LocalTime.of(22, 0),
                LocalTime.of(8, 0),
                10,
                100,
                "987654321"
        );

        estacionamentoId = estacionamentoService
                .cadastrarEstacionamento(estacionamentoDto, dono.getId())
                .id();

        Date birthDate = Date.from(LocalDate.of(1995, 5, 20)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        testGerenteRequest = new GerenteRequest(
                "João",
                "joao@gmail.com",
                "senha123",
                birthDate,
                "11111111111",
                estacionamentoId
        );

        token = jwtService.generateToken(dono.getEmail(), dono.getRole().name());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + token);
    }

    @Test
    void deveCadastrarGerenteValido() throws Exception {
        mockMvc.perform(withAuth(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testGerenteRequest))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.cpfOuCnpj").value("11111111111"))
                .andExpect(jsonPath("$.estacionamentoId").value(estacionamentoId));
    }

    @Test
    void deveAtualizarGerente() throws Exception {
        var response = mockMvc.perform(withAuth(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testGerenteRequest))))
                .andReturn().getResponse().getContentAsString();

        var gerente = objectMapper.readValue(response, GerenteResponse.class);

        var updateDto = new GerenteRequest(
                "Carlos",
                "carlos@gmail.com",
                "senha123",
                testGerenteRequest.dataNascimento(),
                "22222222222",
                estacionamentoId
        );

        mockMvc.perform(withAuth(put("/gerente/" + gerente.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos"));
    }

    @Test
    void deveDeletarGerente() throws Exception {
        var response = mockMvc.perform(withAuth(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testGerenteRequest))))
                .andReturn().getResponse().getContentAsString();

        var gerente = objectMapper.readValue(response, GerenteResponse.class);

        mockMvc.perform(withAuth(delete("/gerente/" + gerente.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarGerentePorId() throws Exception {
        var response = mockMvc.perform(withAuth(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testGerenteRequest))))
                .andReturn().getResponse().getContentAsString();

        var gerente = objectMapper.readValue(response, GerenteResponse.class);

        mockMvc.perform(withAuth(get("/gerente/" + gerente.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.cpfOuCnpj").value("11111111111"))
                .andExpect(jsonPath("$.estacionamentoId").value(estacionamentoId));
    }

    @Test
    void deveRetornarErroAoBuscarGerenteInexistente() throws Exception {
        mockMvc.perform(withAuth(get("/gerente/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("ID buscado não foi encontrado no sistema!"));
    }

    @Test
    void deveRetornarErroAoDeletarGerenteInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/gerente/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Id do gerente buscado não encontrado no sistema!"));
    }
}
