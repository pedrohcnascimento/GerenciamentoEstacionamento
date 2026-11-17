package inpark.gerenciamentoestacionamento.infraestructure_ui.controller.usuario;

import inpark.gerenciamentoestacionamento.application.dto.requests.usuario.DonoRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.usuario.DonoResponse;
import inpark.gerenciamentoestacionamento.application.services.usuario.DonoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dono")
@Tag(
        name = "Dono de estacionamento controller",
        description = "Realiza operações referentes ao CRUD da entidade Dono de estacionamento, eixo central do projeto necessario para a maioria das operações"
)
public class DonoController {
    private final DonoService service;

    public DonoController(DonoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            method = "GET",
            summary = "Listar donos",
            description = "Gera uma lista de todos os donos ativos cadastrados no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de donos retornada com sucesso")
            }
    )
    public ResponseEntity<List<DonoResponse>> listarDonos() {
        return ResponseEntity.status(200).body(service.listarDonos());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar por ID",
            description = "Busca um dono em específico através do ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dono encontrado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Dono não encontrado")
            }
    )
    public ResponseEntity<DonoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(
            method = "POST",
            summary = "Cadastrar dono",
            description = "Cadastra um novo dono no sistema, inicialmente apenas com informações genéricas de usuario.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Dono cadastrado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida")
            }
    )
    public ResponseEntity<DonoResponse> cadastrarDono(@RequestBody DonoRequest dto) {
        return ResponseEntity.status(201).body(service.cadastrarDono(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            method = "PUT",
            summary = "Atualizar dono",
            description = "Atualiza as informações de um dono já cadastrado no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dono atualizado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Dono não encontrado")
            }
    )
    public ResponseEntity<DonoResponse> atualizarDono(@RequestBody DonoRequest dto, @PathVariable Long id) {
        return ResponseEntity.status(200).body(service.atualizarDono(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            method = "DELETE",
            summary = "Deletar dono",
            description = "Desativa (soft delete) um dono já cadastrado do sistema.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Dono desativado com sucesso (sem conteúdo no corpo)"),
                    @ApiResponse(responseCode = "404", description = "Dono não encontrado")
            }
    )
    public ResponseEntity<Void> deletarDono(@PathVariable Long id) {
        service.deletarDono(id);
        return ResponseEntity.status(204).build();
    }
}
