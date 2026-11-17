package inpark.gerenciamentoestacionamento.application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import inpark.gerenciamentoestacionamento.GerenciamentoEstacionamentoApplication;
import inpark.gerenciamentoestacionamento.application.dto.requests.AvaliacaoRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.AvaliacaoResponse;
import inpark.gerenciamentoestacionamento.dao.*;
import inpark.gerenciamentoestacionamento.dao.ClienteDao;
import inpark.gerenciamentoestacionamento.dao.DonoDao;
import inpark.gerenciamentoestacionamento.infraestructure.security.JwtService;
import inpark.gerenciamentoestacionamento.model.entities.Acesso;
import inpark.gerenciamentoestacionamento.model.entities.Carro;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.Reserva;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import inpark.gerenciamentoestacionamento.model.enums.Role;
import inpark.gerenciamentoestacionamento.model.enums.StatusReserva;
import inpark.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GerenciamentoEstacionamentoApplication.class)
@AutoConfigureMockMvc
@Transactional
public class AvaliacaoIntegrationTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    @Autowired private ClienteDao ClienteDao;
    @Autowired private DonoDao DonoDao;
    @Autowired private EstacionamentoDao EstacionamentoDao;
    @Autowired private AvaliacaoDao AvaliacaoDao;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;
    @Autowired private AcessoDao AcessoDao;
    @Autowired private CarroDao CarroDao;
    @Autowired private ReservaDao ReservaDao;

    private String clienteToken;
    private Long clienteId;
    private Long estacionamentoId;

    @BeforeEach
    void setup() throws Exception {
        AcessoDao.deleteAll();
        AvaliacaoDao.deleteAll();
        ReservaDao.deleteAll();
        CarroDao.deleteAll();
        EstacionamentoDao.deleteAll();
        ClienteDao.deleteAll();
        DonoDao.deleteAll();

        Date birthDateDono = Date.from(LocalDate.of(1990, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmailDono = "dono" + System.currentTimeMillis() + "@gmail.com";
        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Pedro Dono")
                .email(uniqueEmailDono)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDateDono)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.DONO)
                .status(true)
                .build();

        DonoDao.save(dono);

        Estacionamento estacionamento = Estacionamento.builder()
                .nome("Estacionamento Teste")
                .endereco("Rua Mockada, 123")
                .CEP("12345-678")
                .numero("123")
                .fotoUrl(null)
                .numeroAlvaraDeFuncionamento("123456789")
                .horaAbertura(LocalTime.of(7, 0))
                .horaFechamento(LocalTime.of(22, 0))
                .maxVagas(50)
                .vagasDisponiveis(50)
                .dono(dono)
                .status(true)
                .build();

        EstacionamentoDao.save(estacionamento);
        estacionamentoId = estacionamento.getId();

        Date birthDateCliente = Date.from(LocalDate.of(1995, 5, 5)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmailCliente = "cliente" + System.currentTimeMillis() + "@teste.com";
        Cliente cliente = Cliente.builder()
                .nome("Cliente Teste")
                .email(uniqueEmailCliente)
                .senha(passwordEncoder.encode("senha456"))
                .dataNascimento(birthDateCliente)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.CLIENTE)
                .status(true)
                .build();

        ClienteDao.save(cliente);
        clienteId = cliente.getId();
        clienteToken = jwtService.generateToken(cliente.getEmail(), cliente.getRole().name());

        Carro carro = Carro.builder()
                .placa("ABC1234")
                .modelo("Teste")
                .cor("Teste")
                .cliente(cliente)
                .status(true)
                .build();

        if (cliente.getCarros() == null) cliente.setCarros(new ArrayList<>());
        cliente.getCarros().add(carro);
        carro = CarroDao.save(carro);

        Reserva reserva = Reserva.builder()
                .cliente(cliente)
                .estacionamento(estacionamento)
                .dataDaReserva(new Date())
                .statusReserva(StatusReserva.PENDENTE)
                .status(true)
                .build();

        if (cliente.getReservas() == null) cliente.setReservas(new ArrayList<>());
        cliente.getReservas().add(reserva);
        ReservaDao.save(reserva);

        Acesso acesso = new Acesso();
        acesso.setCarro(carro);
        acesso.setEstacionamento(estacionamento);
        acesso.setPlacaDoCarro(carro.getPlaca());
        LocalTime entrada = LocalTime.of(10, 0);
        LocalTime saida = LocalTime.of(11, 0);
        acesso.setHoraDeEntrada(entrada);
        acesso.setHoraDeSaida(saida);
        acesso.setTotalHoras((int) Duration.between(entrada, saida).toHours());
        acesso.setValorAPagar(5.0);
        acesso.setStatus(true);

        if (carro.getAcessos() == null) carro.setAcessos(new ArrayList<>());
        carro.getAcessos().add(acesso);
        AcessoDao.save(acesso);
    }

    private AvaliacaoRequest avaliacaoRequestValida() {
        return new AvaliacaoRequest(
                clienteId,
                estacionamentoId,
                (short) 4,
                "Muito bom!",
                LocalDateTime.now()
        );
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + clienteToken);
    }

    @Test
    void deveCadastrarAvaliacaoValida() throws Exception {
        mockMvc.perform(withAuth(post("/avaliacao"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(avaliacaoRequestValida())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cliente.id").value(clienteId))
                .andExpect(jsonPath("$.estacioId").value(estacionamentoId))
                .andExpect(jsonPath("$.nota").value(4))
                .andExpect(jsonPath("$.comentario").value("Muito bom!"));
    }

    @Test
    void deveAtualizarAvaliacao() throws Exception {
        var response = mockMvc.perform(withAuth(post("/avaliacao"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(avaliacaoRequestValida())))
                .andExpect(status().isCreated())
                .andReturn();

        AvaliacaoResponse avaliacaoSalva = objectMapper.readValue(response.getResponse().getContentAsString(), AvaliacaoResponse.class);

        var atualizado = new AvaliacaoRequest(
                clienteId,
                estacionamentoId,
                (short) 5,
                "Excelente! (Atualizado)",
                LocalDateTime.now()
        );

        mockMvc.perform(withAuth(put("/avaliacao/" + avaliacaoSalva.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(avaliacaoSalva.id()))
                .andExpect(jsonPath("$.nota").value(5))
                .andExpect(jsonPath("$.comentario").value("Excelente! (Atualizado)"));
    }

    @Test
    void deveDeletarAvaliacao() throws Exception {
        var response = mockMvc.perform(withAuth(post("/avaliacao"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(avaliacaoRequestValida())))
                .andExpect(status().isCreated())
                .andReturn();

        AvaliacaoResponse avaliacaoSalva = objectMapper.readValue(response.getResponse().getContentAsString(), AvaliacaoResponse.class);

        mockMvc.perform(withAuth(delete("/avaliacao/" + avaliacaoSalva.id())))
                .andExpect(status().isNoContent());

        mockMvc.perform(withAuth(get("/avaliacao/" + avaliacaoSalva.id())))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarAvaliacaoPorId() throws Exception {
        var response = mockMvc.perform(withAuth(post("/avaliacao"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(avaliacaoRequestValida())))
                .andExpect(status().isCreated())
                .andReturn();

        AvaliacaoResponse avaliacaoSalva = objectMapper.readValue(response.getResponse().getContentAsString(), AvaliacaoResponse.class);

        mockMvc.perform(withAuth(get("/avaliacao/" + avaliacaoSalva.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(avaliacaoSalva.id()))
                .andExpect(jsonPath("$.nota").value(4))
                .andExpect(jsonPath("$.comentario").value("Muito bom!"));
    }

    @Test
    void deveListarAvaliacoes() throws Exception {
        mockMvc.perform(withAuth(post("/avaliacao"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(avaliacaoRequestValida())))
                .andExpect(status().isCreated());

        var segundaAvaliacao = new AvaliacaoRequest(
                clienteId,
                estacionamentoId,
                (short) 5,
                "Segunda avaliação",
                LocalDateTime.now()
        );

        mockMvc.perform(withAuth(post("/avaliacao"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(segundaAvaliacao)))
                .andExpect(status().isCreated());

        mockMvc.perform(withAuth(get("/avaliacao")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].nota").value(4))
                .andExpect(jsonPath("$[1].nota").value(5));
    }

    @Test
    void deveRetornarErroAoBuscarAvaliacaoInexistente() throws Exception {
        mockMvc.perform(withAuth(get("/avaliacao/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("ID buscado não foi encontrado no sistema!"));
    }

    @Test
    void deveRetornarErroAoDeletarAvaliacaoInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/avaliacao/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Não foi possivel encontrar a avalição buscada"));
    }
}
