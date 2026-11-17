package inpark.gerenciamentoestacionamento.infraestructure_ui.controller;

import inpark.gerenciamentoestacionamento.application.dto.requests.AcessoRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.AvaliacaoRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.EstacionamentoRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.ReservaRequest;
import inpark.gerenciamentoestacionamento.application.dto.requests.ValorRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.AcessoResponse;
import inpark.gerenciamentoestacionamento.application.dto.response.AvaliacaoResponse;
import inpark.gerenciamentoestacionamento.application.dto.response.EstacionamentoResponse;
import inpark.gerenciamentoestacionamento.application.dto.response.ReservaResponse;
import inpark.gerenciamentoestacionamento.application.dto.response.ValorResponse;
import inpark.gerenciamentoestacionamento.application.services.AcessoService;
import inpark.gerenciamentoestacionamento.application.services.AvaliacaoService;
import inpark.gerenciamentoestacionamento.application.services.EstacionamentoService;
import inpark.gerenciamentoestacionamento.application.services.ReservaService;
import inpark.gerenciamentoestacionamento.application.services.ValorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/estacionamento")
@Tag(
        name = "5. Estacionamentos",
        description = "CRUD de estacionamentos e rotas aninhadas 1:N (reservas, tarifas, acessos e avaliações)"
)
public class EstacionamentoController {
    private final EstacionamentoService service;
    private final ReservaService reservaService;
    private final ValorService valorService;
    private final AcessoService acessoService;
    private final AvaliacaoService avaliacaoService;

    public EstacionamentoController(EstacionamentoService service,
                                    ReservaService reservaService,
                                    ValorService valorService,
                                    AcessoService acessoService,
                                    AvaliacaoService avaliacaoService) {
        this.service = service;
        this.reservaService = reservaService;
        this.valorService = valorService;
        this.acessoService = acessoService;
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping
    @SecurityRequirements
    @Operation(
            summary = "Listar todos os estacionamentos",
            description = "Acesso público. Retorna todos os estacionamentos ativos cadastrados no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de estacionamentos retornada com sucesso")
            }
    )
    public ResponseEntity<List<EstacionamentoResponse>> listarEstacionamentos() {
        return ResponseEntity.status(200).body(service.listarTodosOsEstacionamentos());
    }

    @GetMapping("/{id}")
    @SecurityRequirements
    @Operation(
            summary = "Buscar estacionamento por ID",
            description = "Acesso público. Busca um estacionamento específico através do seu ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estacionamento encontrado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado")
            }
    )
    public ResponseEntity<EstacionamentoResponse> buscarPorId(@Parameter(description = "ID do estacionamento") @PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DONO')")
    @PostMapping("/{id}")
    @Operation(
            summary = "Cadastrar estacionamento para um dono",
            description = "Requer perfil DONO ou ADMIN. Cadastra um novo estacionamento vinculado ao dono informado no parâmetro {id} (equivale a POST /dono/{id}/estacionamentos).",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Estacionamento cadastrado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado"),
                    @ApiResponse(responseCode = "409", description = "Conflito de dados (alvará ou escritura já cadastrados)")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do estacionamento a ser cadastrado",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = EstacionamentoRequest.class),
                            examples = @ExampleObject(value = """
                                  {
                                    "nome": "EstacioPlay",
                                    "endereco": "Rua das Flores",
                                    "CEP": "12345-678",
                                    "numero": "123",
                                    "foto": "foto.jpg",
                                    "numeroAlvaraDeFuncionamento": "123456789",
                                    "horaFechamento": "22:00:00",
                                    "horaAbertura": "08:00:00",
                                    "vagasPreferenciais": 10,
                                    "maximoDeVagas": 100,
                                    "numeroDeEscrituraImovel": "987654321"
                                  }
                                  """
                            )
                    )
            )
    )
    public ResponseEntity<EstacionamentoResponse> cadastrarEstacionamento(@RequestBody @Valid EstacionamentoRequest dto, @Parameter(description = "ID do dono do estacionamento") @PathVariable Long id) {
        EstacionamentoResponse criado = service.cadastrarEstacionamento(dto, id);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/estacionamento/{estId}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(
            method = "PUT",
            summary = "Atualizar um estacionamento existente",
            description = "Atualiza os dados de um estacionamento existente com base no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estacionamento atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado")
            }
    )
    public ResponseEntity<EstacionamentoResponse> atualizarEstacionamento(@RequestBody @Valid EstacionamentoRequest dto, @PathVariable Long id) {
        return ResponseEntity.status(200).body(service.atualizarEstacionamento(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            method = "DELETE",
            summary = "Desativar um estacionamento",
            description = "Desativa (soft delete) um estacionamento existente com base no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Estacionamento desativado com sucesso (sem conteúdo no corpo)"),
                    @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado"),
                    @ApiResponse(responseCode = "409", description = "Estacionamento possui registros vinculados")
            }
    )
    public ResponseEntity<Void> desativarEstacionamento(@PathVariable Long id) {
        service.desativarEstacionamento(id);
        return ResponseEntity.status(204).build();
    }

    // ==================== ROTAS ANINHADAS 1:N ====================

    @GetMapping("/{id}/reservas")
    @SecurityRequirements
    @Operation(
            summary = "Listar reservas do estacionamento (1:N)",
            description = "Acesso público. Lista todas as reservas ativas vinculadas ao estacionamento informado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de reservas retornada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado")
            }
    )
    public ResponseEntity<List<ReservaResponse>> listarReservasDoEstacionamento(@Parameter(description = "ID do estacionamento") @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.listarPorEstacionamento(id));
    }

    @PostMapping("/{id}/reservas")
    @Operation(
            summary = "Cadastrar reserva no estacionamento (1:N)",
            description = "Requer perfil CLIENTE, DONO, GERENTE ou ADMIN. Cria uma reserva vinculada ao estacionamento informado na URL (retorna 409 se não houver vagas).",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Reserva cadastrada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos (Bean Validation)"),
                    @ApiResponse(responseCode = "404", description = "Estacionamento ou cliente não encontrado"),
                    @ApiResponse(responseCode = "409", description = "Sem vagas disponíveis para o período")
            }
    )
    public ResponseEntity<ReservaResponse> cadastrarReservaNoEstacionamento(@Parameter(description = "ID do estacionamento") @PathVariable Long id, @RequestBody @Valid ReservaRequest dto) {
        ReservaRequest req = new ReservaRequest(dto.clienteId(), id, dto.dataDaReserva(), dto.horaDaReserva(), dto.statusReserva());
        ReservaResponse criado = reservaService.cadastrarReserva(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{reservaId}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping("/{id}/valores")
    @SecurityRequirements
    @Operation(
            summary = "Listar tarifas do estacionamento (1:N)",
            description = "Acesso público. Lista todas as tabelas de preços e tarifas ativas do estacionamento informado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de tarifas retornada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado")
            }
    )
    public ResponseEntity<List<ValorResponse>> listarValoresDoEstacionamento(@Parameter(description = "ID do estacionamento") @PathVariable Long id) {
        return ResponseEntity.ok(valorService.listarPorEstacionamento(id));
    }

    @PostMapping("/{id}/valores")
    @Operation(
            summary = "Cadastrar tarifa no estacionamento (1:N)",
            description = "Requer perfil DONO ou ADMIN. Cadastra uma nova tarifa vinculada ao estacionamento informado na URL.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tarifa cadastrada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos (Bean Validation)"),
                    @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado")
            }
    )
    public ResponseEntity<ValorResponse> cadastrarValorNoEstacionamento(@Parameter(description = "ID do estacionamento") @PathVariable Long id, @RequestBody @Valid ValorRequest dto) {
        ValorRequest req = new ValorRequest(dto.tipoDeCobranca(), dto.tipoDePagamento(), dto.preco(), dto.periodo(), id);
        ValorResponse criado = valorService.cadastrarValor(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{valorId}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping("/{id}/acessos")
    @SecurityRequirements
    @Operation(
            summary = "Listar acessos do estacionamento (1:N)",
            description = "Acesso público. Lista todos os registros de entrada e saída de veículos do estacionamento informado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de acessos retornada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado")
            }
    )
    public ResponseEntity<List<AcessoResponse>> listarAcessosDoEstacionamento(@Parameter(description = "ID do estacionamento") @PathVariable Long id) {
        return ResponseEntity.ok(acessoService.listarPorEstacionamento(id));
    }

    @PostMapping("/{id}/acessos")
    @Operation(
            summary = "Registrar acesso no estacionamento (1:N)",
            description = "Requer perfil CLIENTE, DONO, GERENTE ou ADMIN. Registra a entrada/saída de um veículo no estacionamento informado na URL.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Acesso registrado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos (Bean Validation)"),
                    @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado")
            }
    )
    public ResponseEntity<AcessoResponse> cadastrarAcessoNoEstacionamento(@Parameter(description = "ID do estacionamento") @PathVariable Long id, @RequestBody @Valid AcessoRequest dto) {
        AcessoRequest req = new AcessoRequest(dto.carroId(), dto.placaDoCarro(), dto.horaDeEntrada(), dto.horaDeSaida(), dto.valorAPagar(), id);
        AcessoResponse criado = acessoService.cadastrarAcesso(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{acessoId}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping("/{id}/avaliacoes")
    @SecurityRequirements
    @Operation(
            summary = "Listar avaliações do estacionamento (1:N)",
            description = "Acesso público. Lista todas as avaliações ativas recebidas pelo estacionamento informado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de avaliações retornada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado")
            }
    )
    public ResponseEntity<List<AvaliacaoResponse>> listarAvaliacoesDoEstacionamento(@Parameter(description = "ID do estacionamento") @PathVariable Long id) {
        return ResponseEntity.ok(avaliacaoService.listarPorEstacionamento(id));
    }

    @PostMapping("/{id}/avaliacoes")
    @Operation(
            summary = "Cadastrar avaliação no estacionamento (1:N)",
            description = "Requer perfil CLIENTE, DONO, GERENTE ou ADMIN. Registra uma avaliação para o estacionamento informado na URL.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Avaliação cadastrada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Avaliação inválida"),
                    @ApiResponse(responseCode = "404", description = "Estacionamento ou cliente não encontrado")
            }
    )
    public ResponseEntity<AvaliacaoResponse> cadastrarAvaliacaoNoEstacionamento(@Parameter(description = "ID do estacionamento") @PathVariable Long id, @RequestBody @Valid AvaliacaoRequest dto) {
        AvaliacaoRequest req = new AvaliacaoRequest(dto.clienteId(), id, dto.nota(), dto.comentario(), dto.dataDeAvaliacao());
        AvaliacaoResponse criado = avaliacaoService.cadastrarAvaliacao(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{avaliacaoId}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }
}
