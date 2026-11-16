package inpark.gerenciamentoestacionamento.application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import inpark.gerenciamentoestacionamento.GerenciamentoEstacionamentoApplication;
import inpark.gerenciamentoestacionamento.application.dto.requests.EstacionamentoRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.ReservaRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.EstacionamentoResponse;
import inpark.gerenciamentoestacionamento.application.dto.response.ReservaResponse;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.*;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.usuario.ClienteRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.usuario.DonoRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.usuario.GerenteRepository;
import inpark.gerenciamentoestacionamento.infraestructure.security.JwtService;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import inpark.gerenciamentoestacionamento.model.enums.Role;
import inpark.gerenciamentoestacionamento.model.enums.StatusReserva;
import inpark.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GerenciamentoEstacionamentoApplication.class)
@AutoConfigureMockMvc
public class ReservaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private AcessoRepository acessoRepository;
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;
    @Autowired
    private ValorRepository valorRepository;
    @Autowired
    private GerenteRepository gerenteRepository;
    @Autowired
    private CarroRepository carroRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private DonoRepository donoRepository;
    @Autowired
    private EstacionamentoRepository estacionamentoRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    private String clienteToken;
    private Long clienteId;
    private Long estacionamentoId;

    @BeforeEach
    void setup() throws Exception {
        acessoRepository.deleteAll();
        avaliacaoRepository.deleteAll();
        reservaRepository.deleteAll();
        valorRepository.deleteAll();
        gerenteRepository.deleteAll();
        carroRepository.deleteAll();
        estacionamentoRepository.deleteAll();
        clienteRepository.deleteAll();
        donoRepository.deleteAll();

        Date birthDateDono = Date.from(LocalDate.of(1990, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmailDono = "dono" + System.currentTimeMillis() + "@gmail.com";
        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Pedro Dono")
                .email(uniqueEmailDono)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDateDono)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.CLIENTE)
                .status(true)
                .build();

        donoRepository.save(dono);

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

        var responseEstacionamento = mockMvc.perform(withAuth(post("/estacionamento/" + dono.getId()), jwtService.generateToken(dono.getEmail(), dono.getRole().name()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(estacionamentoRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EstacionamentoResponse estacionamentoResponse = objectMapper.readValue(responseEstacionamento, EstacionamentoResponse.class);
        estacionamentoId = estacionamentoResponse.id();

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

        clienteRepository.save(cliente);
        clienteId = cliente.getId();
        clienteToken = jwtService.generateToken(cliente.getEmail(), cliente.getRole().name());
    }

    private ReservaRequest reservaRequestValido() {
        return new ReservaRequest(
                clienteId,
                estacionamentoId,
                new Date(System.currentTimeMillis() + 86400000),
                Time.valueOf("14:30:00"),
                StatusReserva.PENDENTE
        );
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder, String token) {
        return builder.header("Authorization", "Bearer " + token);
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + clienteToken);
    }

    @Test
    void deveCadastrarReservaValida() throws Exception {
        mockMvc.perform(withAuth(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reservaRequestValido()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusReserva").value("PENDENTE"));
    }

    @Test
    void deveAtualizarReserva() throws Exception {
        var response = mockMvc.perform(withAuth(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reservaRequestValido()))))
                .andExpect(status().isCreated())
                .andReturn();

        ReservaResponse reservaSalva = objectMapper.readValue(response.getResponse().getContentAsString(), ReservaResponse.class);

        var atualizado = new ReservaRequest(
                clienteId,
                estacionamentoId,
                new Date(System.currentTimeMillis() + 172800000),
                Time.valueOf("15:00:00"),
                StatusReserva.ACEITA
        );

        mockMvc.perform(withAuth(put("/reserva/" + reservaSalva.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(atualizado))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusReserva").value("ACEITA"))
                .andExpect(jsonPath("$.horaDaReserva").value("15:00:00"));
    }

    @Test
    void deveDeletarReserva() throws Exception {
        var response = mockMvc.perform(withAuth(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reservaRequestValido()))))
                .andExpect(status().isCreated())
                .andReturn();

        ReservaResponse reservaSalva = objectMapper.readValue(response.getResponse().getContentAsString(), ReservaResponse.class);

        mockMvc.perform(withAuth(delete("/reserva/" + reservaSalva.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarReservaPorId() throws Exception {
        var response = mockMvc.perform(withAuth(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reservaRequestValido()))))
                .andExpect(status().isCreated())
                .andReturn();

        ReservaResponse reservaSalva = objectMapper.readValue(response.getResponse().getContentAsString(), ReservaResponse.class);

        mockMvc.perform(withAuth(get("/reserva/" + reservaSalva.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservaSalva.id()));
    }

    @Test
    void deveListarReservas() throws Exception {
        mockMvc.perform(withAuth(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reservaRequestValido()))))
                .andExpect(status().isCreated());

        mockMvc.perform(withAuth(get("/reserva")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void deveRetornarErroAoBuscarReservaInexistente() throws Exception {
        mockMvc.perform(withAuth(get("/reserva/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("ID buscado não foi encontrado no sistema!"));
    }

    @Test
    void deveRetornarErroAoDeletarReservaInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/reserva/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("ID de reserva buscado não encontrado no sistema!"));
    }
}
