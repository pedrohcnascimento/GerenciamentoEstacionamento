package inpark.gerenciamentoestacionamento.infraestructure_ui.controller;

import inpark.gerenciamentoestacionamento.application.dto.requests.ValorRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.ValorResponse;
import inpark.gerenciamentoestacionamento.application.services.ValorService;
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
@RequestMapping("/valor")
@Tag(
        name = "07. Tarifas e Valores",
        description = "CRUD direto de tabelas de preços e cobranças (requer perfil DONO ou ADMIN)"
)
public class ValorController {
    private final ValorService service;

    public ValorController(ValorService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Listar valores",
            description = "Lista todos os valores ativos cadastrados no sistema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de valores retornada com sucesso")
            }
    )
    public ResponseEntity<List<ValorResponse>> listarValor() {
        return ResponseEntity.status(200).body(service.listarValor());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar por ID",
            description = "Busca uma tarifa em específico através do ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Valor encontrado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Valor não encontrado")
            }
    )
    public ResponseEntity<ValorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar valor",
            description = "Atualiza um valor cadastrado no sistema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Valor atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "404", description = "Valor ou estacionamento não encontrado")
            }
    )
    public ResponseEntity<ValorResponse> atualizarValor(@RequestBody @Valid ValorRequest dto, @PathVariable Long id) {
        return ResponseEntity.status(200).body(service.atualizarValor(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar valor",
            description = "Desativa (soft delete) um valor cadastrado no sistema",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Valor desativado com sucesso (sem conteúdo no corpo)"),
                    @ApiResponse(responseCode = "404", description = "Valor não encontrado")
            }
    )
    public ResponseEntity<Void> deletarValor(@PathVariable Long id) {
        service.deletarValor(id);
        return ResponseEntity.status(204).build();
    }
}
