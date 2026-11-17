package parkwise.gerenciamentoestacionamento.infraestructure_ui.controller.usuario;

import parkwise.gerenciamentoestacionamento.application.dto.requests.usuario.DonoRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.usuario.DonoResponse;
import parkwise.gerenciamentoestacionamento.application.services.usuario.DonoService;
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
@RequestMapping("/dono")
@Tag(
        name = "03. Donos de Estacionamento",
        description = "Cadastro, consulta, atualização e remoção de proprietários"
)
public class DonoController {
    private final DonoService service;

    public DonoController(DonoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
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
    @SecurityRequirements
    @Operation(
            summary = "Cadastrar dono",
            description = "Acesso público. Cadastra um novo proprietário de estacionamento no sistema.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Dono cadastrado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "409", description = "Conflito de dados (e-mail já cadastrado)")
            }
    )
    public ResponseEntity<DonoResponse> cadastrarDono(@RequestBody @Valid DonoRequest dto) {
        DonoResponse criado = service.cadastrarDono(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar dono",
            description = "Atualiza as informações de um dono já cadastrado no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dono atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "404", description = "Dono não encontrado")
            }
    )
    public ResponseEntity<DonoResponse> atualizarDono(@RequestBody @Valid DonoRequest dto, @PathVariable Long id) {
        return ResponseEntity.status(200).body(service.atualizarDono(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar dono",
            description = "Desativa (soft delete) um dono já cadastrado do sistema.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Dono desativado com sucesso (sem conteúdo no corpo)"),
                    @ApiResponse(responseCode = "404", description = "Dono não encontrado"),
                    @ApiResponse(responseCode = "409", description = "Dono possui registros vinculados")
            }
    )
    public ResponseEntity<Void> deletarDono(@PathVariable Long id) {
        service.deletarDono(id);
        return ResponseEntity.status(204).build();
    }
}
