package inpark.gerenciamentoestacionamento.infraestructure_ui.controller;

import inpark.gerenciamentoestacionamento.application.dto.requests.AcessoRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.AcessoResponse;
import inpark.gerenciamentoestacionamento.application.services.AcessoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/acesso")
@Tag(
        name = "09. Acessos (Entrada e Saída)",
        description = "CRUD direto de registros de entrada e saída de veículos nos estacionamentos"
)
public class AcessoController {
    private final AcessoService service;

    public AcessoController(AcessoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            method = "GET",
            summary = "Listar todos os acessos",
            description = "Retorna uma lista de todos os acessos ativos cadastrados no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de acessos retornada com sucesso")
            }
    )
    public ResponseEntity<List<AcessoResponse>> listarAcessos() {
        return ResponseEntity.status(200).body(service.listarAcessos());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar por ID",
            description = "Busca um acesso em específico através do ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Acesso encontrado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Acesso não encontrado")
            }
    )
    public ResponseEntity<AcessoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(
            method = "PUT",
            summary = "Atualizar um acesso",
            description = "Atualiza um acesso já cadastrado no sistema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Acesso atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "404", description = "Acesso ou estacionamento não encontrado")
            }
    )
    public ResponseEntity<AcessoResponse> atualizarAcesso(@PathVariable Long id, @RequestBody @Valid AcessoRequest dto) {
        return ResponseEntity.status(200).body(service.atualizarAcesso(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            method = "DELETE",
            summary = "Deletar um acesso",
            description = "Desativa (soft delete) um acesso já cadastrado no sistema",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Acesso desativado com sucesso (sem conteúdo no corpo)"),
                    @ApiResponse(responseCode = "404", description = "Acesso não encontrado")
            }
    )
    public ResponseEntity<Void> deletarAcesso(@PathVariable Long id) {
        service.deletarAcesso(id);
        return ResponseEntity.status(204).build();
    }
}
