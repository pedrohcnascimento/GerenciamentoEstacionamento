package parkwise.gerenciamentoestacionamento.infraestructure_ui.controller.usuario;

import parkwise.gerenciamentoestacionamento.application.dto.requests.EstacionamentoRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.EstacionamentoResponse;
import parkwise.gerenciamentoestacionamento.application.services.EstacionamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
        name = "12. Rotas 1:N — Dono",
        description = "Operações aninhadas vinculadas a um Dono (estacionamentos)"
)
public class DonoRelacionamentoController {
    private final EstacionamentoService estacionamentoService;

    public DonoRelacionamentoController(EstacionamentoService estacionamentoService) {
        this.estacionamentoService = estacionamentoService;
    }

    @GetMapping("/{id}/estacionamentos")
    @Operation(
            summary = "Listar estacionamentos do proprietário (1:N)",
            description = "Requer perfil DONO ou ADMIN. Lista todos os estacionamentos ativos pertencentes ao proprietário informado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de estacionamentos retornada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
            }
    )
    public ResponseEntity<List<EstacionamentoResponse>> listarEstacionamentosDoDono(@Parameter(description = "ID do dono de estacionamento") @PathVariable Long id) {
        return ResponseEntity.ok(estacionamentoService.listarPorDono(id));
    }

    @PostMapping("/{id}/estacionamentos")
    @Operation(
            summary = "Cadastrar estacionamento para o proprietário (1:N)",
            description = "Requer perfil DONO ou ADMIN. Cadastra um novo estacionamento vinculado automaticamente ao proprietário informado na URL.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Estacionamento cadastrado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos (Bean Validation)"),
                    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado"),
                    @ApiResponse(responseCode = "409", description = "Conflito de dados (alvará ou escritura já cadastrados)")
            }
    )
    public ResponseEntity<EstacionamentoResponse> cadastrarEstacionamentoDoDono(@Parameter(description = "ID do dono de estacionamento") @PathVariable Long id, @RequestBody @Valid EstacionamentoRequest dto) {
        EstacionamentoResponse criado = estacionamentoService.cadastrarEstacionamento(dto, id);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{estacionamentoId}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }
}
