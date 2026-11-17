package parkwise.gerenciamentoestacionamento.infraestructure_ui.controller;

import parkwise.gerenciamentoestacionamento.application.dto.requests.AvaliacaoRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.AvaliacaoResponse;
import parkwise.gerenciamentoestacionamento.application.services.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/avaliacao")
@Tag(
        name = "10. Avaliações",
        description = "CRUD direto de avaliações de estacionamentos"
)
public class AvaliacaoController {
    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @GetMapping
    @SecurityRequirements
    @Operation(
            summary = "Listar todas as avaliações",
            description = "Acesso público. Retorna todas as avaliações ativas cadastradas no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de avaliações retornada com sucesso")
            }
    )
    public ResponseEntity<List<AvaliacaoResponse>> listarAvaliacoes() {
        return ResponseEntity.status(200).body(service.listarAvaliacoes());
    }

    @GetMapping("/{id}")
    @SecurityRequirements
    @Operation(
            summary = "Buscar avaliação por ID",
            description = "Acesso público. Busca uma avaliação específica através do seu ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Avaliação encontrada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
            }
    )
    public ResponseEntity<AvaliacaoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(
            method = "PUT",
            summary = "Atualizar uma avaliação",
            description = "Atualiza uma avaliação já cadastrada no sistema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Avaliação atualizada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Avaliação inválida ou prazo de edição expirado"),
                    @ApiResponse(responseCode = "404", description = "Avaliação, cliente ou estacionamento não encontrado")
            }
    )
    public ResponseEntity<AvaliacaoResponse> atualizarAvaliacao(@PathVariable Long id, @RequestBody @Valid AvaliacaoRequest dto) {
        return ResponseEntity.status(200).body(service.atualizarAvaliacao(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            method = "DELETE",
            summary = "Deletar uma avaliação",
            description = "Desativa (soft delete) uma avaliação já cadastrada no sistema",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Avaliação desativada com sucesso (sem conteúdo no corpo)"),
                    @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
            }
    )
    public ResponseEntity<Void> deletarAvaliacao(@PathVariable Long id) {
        service.deletarAvaliacao(id);
        return ResponseEntity.status(204).build();
    }
}
