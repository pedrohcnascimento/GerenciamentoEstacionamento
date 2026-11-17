package parkwise.gerenciamentoestacionamento.infraestructure_ui.controller;

import parkwise.gerenciamentoestacionamento.application.dto.requests.AcessoRequest;
import parkwise.gerenciamentoestacionamento.application.dto.requests.AvaliacaoRequest;
import parkwise.gerenciamentoestacionamento.application.dto.requests.ReservaRequest;
import parkwise.gerenciamentoestacionamento.application.dto.requests.ValorRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.AcessoResponse;
import parkwise.gerenciamentoestacionamento.application.dto.response.AvaliacaoResponse;
import parkwise.gerenciamentoestacionamento.application.dto.response.ReservaResponse;
import parkwise.gerenciamentoestacionamento.application.dto.response.ValorResponse;
import parkwise.gerenciamentoestacionamento.application.services.AcessoService;
import parkwise.gerenciamentoestacionamento.application.services.AvaliacaoService;
import parkwise.gerenciamentoestacionamento.application.services.ReservaService;
import parkwise.gerenciamentoestacionamento.application.services.ValorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/estacionamento")
@Tag(
        name = "13. Rotas 1:N — Estacionamento",
        description = "Operações aninhadas vinculadas a um Estacionamento (reservas, tarifas, acessos e avaliações)"
)
public class EstacionamentoRelacionamentoController {
    private final ReservaService reservaService;
    private final ValorService valorService;
    private final AcessoService acessoService;
    private final AvaliacaoService avaliacaoService;

    public EstacionamentoRelacionamentoController(ReservaService reservaService,
                                                    ValorService valorService,
                                                    AcessoService acessoService,
                                                    AvaliacaoService avaliacaoService) {
        this.reservaService = reservaService;
        this.valorService = valorService;
        this.acessoService = acessoService;
        this.avaliacaoService = avaliacaoService;
    }

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
