package inpark.gerenciamentoestacionamento.infraestructure_ui.controller.usuario;

import inpark.gerenciamentoestacionamento.application.dto.requests.CarroRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.usuario.ClienteRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.CarroResponse;
import inpark.gerenciamentoestacionamento.application.dto.response.ReservaResponse;
import inpark.gerenciamentoestacionamento.application.dto.response.usuario.ClienteResponse;
import inpark.gerenciamentoestacionamento.application.services.CarroService;
import inpark.gerenciamentoestacionamento.application.services.ReservaService;
import inpark.gerenciamentoestacionamento.application.services.usuario.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cliente")
@Tag(
        name = "Cliente controller",
        description = "Realiza operações relacionadas a clientes."
)
public class ClienteController {
    private final ClienteService service;
    private final CarroService carroService;
    private final ReservaService reservaService;

    public ClienteController(ClienteService service, CarroService carroService, ReservaService reservaService) {
        this.service = service;
        this.carroService = carroService;
        this.reservaService = reservaService;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os clientes",
            description = "Retorna uma lista de todos os clientes ativos cadastrados no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
            }
    )
    public ResponseEntity<List<ClienteResponse>> listarClientes() {
        return ResponseEntity.status(200).body(service.listarClientes());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar por ID",
            description = "Busca um cliente em específico através do ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            }
    )
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar um novo cliente",
            description = "Cadastra um novo cliente no sistema com base nos dados fornecidos.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "409", description = "Conflito de dados (e-mail já cadastrado)")
            }
    )
    public ResponseEntity<ClienteResponse> cadastrarCliente(@RequestBody @Valid ClienteRequest dto) {
        ClienteResponse criado = service.cadastrarCliente(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar um cliente existente",
            description = "Atualiza os dados de um cliente existente com base no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            }
    )
    public ResponseEntity<ClienteResponse> atualizarCliente(@RequestBody @Valid ClienteRequest dto, @PathVariable Long id) {
        return ResponseEntity.status(200).body(service.atualizarCliente(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar um cliente",
            description = "Desativa (soft delete) um cliente existente com base no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso (sem conteúdo no corpo)"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
                    @ApiResponse(responseCode = "409", description = "Cliente possui registros vinculados")
            }
    )
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        service.deletarCliente(id);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/{id}/carros")
    @Operation(
            summary = "Listar carros do cliente (1:N)",
            description = "Retorna os carros vinculados ao cliente informado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de carros retornada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            }
    )
    public ResponseEntity<List<CarroResponse>> listarCarrosDoCliente(@PathVariable Long id) {
        return ResponseEntity.ok(carroService.listarPorCliente(id));
    }

    @PostMapping("/{id}/carros")
    @Operation(
            summary = "Cadastrar carro para o cliente (1:N)",
            description = "Cadastra um novo carro vinculado ao cliente informado.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Carro cadastrado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
                    @ApiResponse(responseCode = "409", description = "Placa já cadastrada")
            }
    )
    public ResponseEntity<CarroResponse> cadastrarCarroDoCliente(@PathVariable Long id, @RequestBody @Valid CarroRequest dto) {
        CarroRequest req = new CarroRequest(id, dto.placa(), dto.modelo(), dto.cor());
        CarroResponse criado = carroService.cadastrarCarro(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{carroId}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping("/{id}/reservas")
    @Operation(
            summary = "Listar reservas do cliente (1:N)",
            description = "Retorna as reservas realizadas pelo cliente informado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de reservas retornada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            }
    )
    public ResponseEntity<List<ReservaResponse>> listarReservasDoCliente(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.listarPorCliente(id));
    }
}
