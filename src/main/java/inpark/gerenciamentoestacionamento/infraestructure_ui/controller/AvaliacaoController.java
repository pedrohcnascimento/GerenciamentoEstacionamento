package inpark.gerenciamentoestacionamento.infraestructure_ui.controller;

import inpark.gerenciamentoestacionamento.application.dto.requests.AvaliacaoRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.AvaliacaoResponse;
import inpark.gerenciamentoestacionamento.application.services.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacao")
@Tag(
        name = "Avaliação controller",
        description = "Realiza operações referentes à avaliações de estacionamento"
)
public class AvaliacaoController {
    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            method = "GET",
            summary = "Listar todas as avaliações",
            description = "Retorna uma lista de todas as avaliações ativas cadastradas no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de avaliações retornada com sucesso")
            }
    )
    public ResponseEntity<List<AvaliacaoResponse>> listarAvaliacoes() {
        return ResponseEntity.status(200).body(service.listarAvaliacoes());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar por ID",
            description = "Busca uma avaliação em específico através do ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Avaliação encontrada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
            }
    )
    public ResponseEntity<AvaliacaoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(
            method = "POST",
            summary = "Cadastrar nova avaliação",
            description = "Adiciona uma nova avaliação ao sistema",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Avaliação cadastrada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Avaliação inválida ou comentário muito longo"),
                    @ApiResponse(responseCode = "404", description = "Cliente ou estacionamento não encontrado")
            }
    )
    public ResponseEntity<AvaliacaoResponse> cadastrarAvaliacao(@RequestBody AvaliacaoRequest dto) {
        return ResponseEntity.status(201).body(service.cadastrarAvaliacao(dto));
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
    public ResponseEntity<AvaliacaoResponse> atualizarAvaliacao(@PathVariable Long id, @RequestBody AvaliacaoRequest dto) {
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
