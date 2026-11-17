package inpark.gerenciamentoestacionamento.application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import inpark.gerenciamentoestacionamento.GerenciamentoEstacionamentoApplication;
import inpark.gerenciamentoestacionamento.application.dto.requests.EstacionamentoRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.ValorRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.EstacionamentoResponse;
import inpark.gerenciamentoestacionamento.application.dto.response.ValorResponse;
import inpark.gerenciamentoestacionamento.dao.*;
import inpark.gerenciamentoestacionamento.dao.DonoDao;
import inpark.gerenciamentoestacionamento.dao.GerenteDao;
import inpark.gerenciamentoestacionamento.infraestructure.security.JwtService;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import inpark.gerenciamentoestacionamento.model.enums.*;
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
public class ValorIntegrationTest {

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
    private GerenteDao GerenteDao;
    @Autowired
    private ValorDao ValorDao;
    @Autowired
    private DonoDao DonoDao;
    @Autowired
    private EstacionamentoDao EstacionamentoDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    private String token;
    private Long estacionamentoId;

    @BeforeEach
    void setUp() throws Exception {
        AcessoDao.deleteAll();
        AvaliacaoDao.deleteAll();
        ReservaDao.deleteAll();
        ValorDao.deleteAll();
        GerenteDao.deleteAll();
        EstacionamentoDao.deleteAll();
        DonoDao.deleteAll();

        Date birthDate = Date.from(LocalDate.of(2000, 9, 12)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmail = "pedro" + System.currentTimeMillis() + "@gmail.com";

        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Pedro")
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

        var response = mockMvc.perform(withAuth(post("/dono/" + dono.getId() + "/estacionamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(estacionamentoRequest))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EstacionamentoResponse estacionamentoResponse = objectMapper.readValue(response, EstacionamentoResponse.class);
        estacionamentoId = estacionamentoResponse.id();
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request) {
        return request.header("Authorization", "Bearer " + token);
    }

    private ValorRequest criarValorRequest(Double preco) {
        return new ValorRequest(Cobranca.PORHORA, Metodo.DINHEIRO, preco, Periodo.FINALDESEMANA, estacionamentoId);
    }

    @Test
    void deveCadastrarValorValido() throws Exception {
        var dto = criarValorRequest(10.0);

        mockMvc.perform(withAuth(post("/estacionamento/" + estacionamentoId + "/valores"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.preco").value(10.0));
    }

    @Test
    void deveAtualizarValor() throws Exception {
        var dto = criarValorRequest(10.0);
        var salvoJson = mockMvc.perform(withAuth(post("/estacionamento/" + estacionamentoId + "/valores"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var valorSalvo = objectMapper.readValue(salvoJson, ValorResponse.class);

        var atualizado = new ValorRequest(Cobranca.PORVAGA, Metodo.PIX, 20.0, Periodo.FINALDESEMANA, estacionamentoId);
        mockMvc.perform(withAuth(put("/valor/" + valorSalvo.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preco").value(20.0))
                .andExpect(jsonPath("$.tipoDeCobranca").value("PORVAGA"))
                .andExpect(jsonPath("$.tipoDePagamento").value("PIX"));
    }

    @Test
    void deveDeletarValor() throws Exception {
        var dto = criarValorRequest(10.0);
        var salvo = mockMvc.perform(withAuth(post("/estacionamento/" + estacionamentoId + "/valores"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var valorSalvo = objectMapper.readValue(salvo, ValorResponse.class);

        mockMvc.perform(withAuth(delete("/valor/" + valorSalvo.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarValorPorId() throws Exception {
        var dto = criarValorRequest(10.0);
        var salvo = mockMvc.perform(withAuth(post("/estacionamento/" + estacionamentoId + "/valores"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var valorSalvo = objectMapper.readValue(salvo, ValorResponse.class);

        mockMvc.perform(withAuth(get("/valor/" + valorSalvo.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preco").value(10.0));
    }

    @Test
    void deveListarValores() throws Exception {
        var dto = criarValorRequest(10.0);
        mockMvc.perform(withAuth(post("/estacionamento/" + estacionamentoId + "/valores"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(withAuth(get("/valor")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].preco").value(10.0));
    }
}
