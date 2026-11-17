package inpark.gerenciamentoestacionamento.infraestructure_ui.controller;

import inpark.gerenciamentoestacionamento.application.dto.requests.EstacionamentoRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.EstacionamentoResponse;
import inpark.gerenciamentoestacionamento.application.services.EstacionamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estacionamento")
@Tag(
        name = "5. Estacionamentos",
        description = "Consulta, atualização e remoção de estacionamentos"
)
public class EstacionamentoController {
    private final EstacionamentoService service;

    public EstacionamentoController(EstacionamentoService service) {
        this.service = service;
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

    @PutMapping("/{id}")
    @Operation(
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
}
