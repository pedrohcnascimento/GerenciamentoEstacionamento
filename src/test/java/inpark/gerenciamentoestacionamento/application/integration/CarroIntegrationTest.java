package inpark.gerenciamentoestacionamento.application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import inpark.gerenciamentoestacionamento.GerenciamentoEstacionamentoApplication;
import inpark.gerenciamentoestacionamento.application.dto.requests.CarroRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.CarroResponse;
import inpark.gerenciamentoestacionamento.dao.AcessoDao;
import inpark.gerenciamentoestacionamento.dao.AvaliacaoDao;
import inpark.gerenciamentoestacionamento.dao.CarroDao;
import inpark.gerenciamentoestacionamento.dao.ReservaDao;
import inpark.gerenciamentoestacionamento.dao.ClienteDao;
import inpark.gerenciamentoestacionamento.infraestructure.security.JwtService;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
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
import java.time.ZoneId;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = GerenciamentoEstacionamentoApplication.class)
@AutoConfigureMockMvc
public class CarroIntegrationTest {

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
    private ClienteDao ClienteDao;
    @Autowired
    private CarroDao CarroDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    private String token;
    private Long clienteId;

    @BeforeEach
    void setUp() throws Exception {
        AcessoDao.deleteAll();
        AvaliacaoDao.deleteAll();
        ReservaDao.deleteAll();
        CarroDao.deleteAll();
        ClienteDao.deleteAll();

        Date birthDate = Date.from(LocalDate.of(1995, 5, 5)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmail = "cliente" + System.currentTimeMillis() + "@teste.com";
        Cliente cliente = Cliente.builder()
                .nome("Cliente Teste")
                .email(uniqueEmail)
                .senha(passwordEncoder.encode("senha456"))
                .dataNascimento(birthDate)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.CLIENTE)
                .status(true)
                .build();

        ClienteDao.save(cliente);
        clienteId = cliente.getId();

        token = jwtService.generateToken(cliente.getEmail(), cliente.getRole().name());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + token);
    }

    private CarroRequest carroRequestPadrao() {
        return new CarroRequest(clienteId, "EUD8679", "Corsa", "Fuxia");
    }

    @Test
    void deveCadastrarCarroValido() throws Exception {
        mockMvc.perform(withAuth(post("/cliente/" + clienteId + "/carros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(carroRequestPadrao()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("EUD8679"))
                .andExpect(jsonPath("$.modelo").value("Corsa"))
                .andExpect(jsonPath("$.cor").value("Fuxia"));
    }

    @Test
    void deveAtualizarCarro() throws Exception {
        var response = mockMvc.perform(withAuth(post("/cliente/" + clienteId + "/carros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(carroRequestPadrao()))))
                .andExpect(status().isCreated())
                .andReturn();

        CarroResponse carroSalvo = objectMapper.readValue(response.getResponse().getContentAsString(), CarroResponse.class);

        var atualizado = new CarroRequest(clienteId, "ABC1234", "Onix", "Preto");

        mockMvc.perform(withAuth(put("/carro/" + carroSalvo.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(atualizado))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.modelo").value("Onix"))
                .andExpect(jsonPath("$.cor").value("Preto"));
    }

    @Test
    void deveDeletarCarro() throws Exception {
        var response = mockMvc.perform(withAuth(post("/cliente/" + clienteId + "/carros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(carroRequestPadrao()))))
                .andExpect(status().isCreated())
                .andReturn();

        CarroResponse carroSalvo = objectMapper.readValue(response.getResponse().getContentAsString(), CarroResponse.class);

        mockMvc.perform(withAuth(delete("/carro/" + carroSalvo.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarCarroPorId() throws Exception {
        var response = mockMvc.perform(withAuth(post("/cliente/" + clienteId + "/carros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(carroRequestPadrao()))))
                .andExpect(status().isCreated())
                .andReturn();

        CarroResponse carroSalvo = objectMapper.readValue(response.getResponse().getContentAsString(), CarroResponse.class);

        mockMvc.perform(withAuth(get("/carro/" + carroSalvo.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("EUD8679"))
                .andExpect(jsonPath("$.modelo").value("Corsa"))
                .andExpect(jsonPath("$.cor").value("Fuxia"));
    }

    @Test
    void deveListarCarros() throws Exception {
        mockMvc.perform(withAuth(post("/cliente/" + clienteId + "/carros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(carroRequestPadrao()))))
                .andExpect(status().isCreated());

        mockMvc.perform(withAuth(get("/carro")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].placa").value("EUD8679"));
    }
}
