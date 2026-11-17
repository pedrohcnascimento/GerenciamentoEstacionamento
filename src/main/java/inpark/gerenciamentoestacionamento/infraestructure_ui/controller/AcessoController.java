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
        name = "Acesso controller",
        description = "Realiza operações referente ao acesso dos carros aos estacionamentos"
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

    @PostMapping
    @Operation(
            method = "POST",
            summary = "Cadastrar novo acesso",
            description = "Adiciona um novo acesso ao sistema",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Acesso cadastrado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do acesso a ser cadastrado",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AcessoRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "carroId": 1,
                                      "placaDoCarro": "EUD8679",
                                      "horaDeEntrada": "09:38:51",
                                      "horaDeSaida": "19:38:51",
                                      "valorAPagar": 100.00,
                                      "estacioId": 1
                                    }
                                    """)
                    )
            )
    )
    public ResponseEntity<AcessoResponse> cadastrarAcesso(@RequestBody @Valid AcessoRequest dto) {
        AcessoResponse criado = service.cadastrarAcesso(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
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
