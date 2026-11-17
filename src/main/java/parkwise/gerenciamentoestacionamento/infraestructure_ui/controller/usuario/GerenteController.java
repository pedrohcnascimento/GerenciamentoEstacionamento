package parkwise.gerenciamentoestacionamento.infraestructure_ui.controller.usuario;

import parkwise.gerenciamentoestacionamento.application.dto.requests.usuario.GerenteRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.usuario.GerenteResponse;
import parkwise.gerenciamentoestacionamento.application.services.usuario.GerenteService;
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
@RequestMapping("/gerente")
@Tag(
        name = "04. Gerentes",
        description = "CRUD de gerentes vinculados a estacionamentos (requer perfil DONO ou ADMIN)"
)
public class GerenteController {
    private final GerenteService service;

    public GerenteController(GerenteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os gerentes",
            description = "Retorna uma lista de todos os gerentes ativos cadastrados no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de gerentes retornada com sucesso")
            }
    )
    public ResponseEntity<List<GerenteResponse>> listarGerentes() {
        return ResponseEntity.status(200).body(service.listarGerentes());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar por ID",
            description = "Busca um gerente em específico através do ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Gerente encontrado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Gerente não encontrado")
            }
    )
    public ResponseEntity<GerenteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar um novo gerente",
            description = "Cadastra um novo gerente no sistema com base nos dados fornecidos.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Gerente cadastrado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado"),
                    @ApiResponse(responseCode = "409", description = "Conflito de dados (e-mail ou CPF/CNPJ já cadastrado)")
            }
    )
    public ResponseEntity<GerenteResponse> cadastrarGerente(@RequestBody @Valid GerenteRequest dto) {
        GerenteResponse criado = service.cadastrarGerente(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar um gerente existente",
            description = "Atualiza os dados de um gerente existente com base no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Gerente atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "404", description = "Gerente não encontrado")
            }
    )
    public ResponseEntity<GerenteResponse> atualizarGerente(@RequestBody @Valid GerenteRequest dto, @PathVariable Long id) {
        return ResponseEntity.status(200).body(service.atualizarGerente(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar um gerente",
            description = "Desativa (soft delete) um gerente existente com base no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Gerente deletado com sucesso (sem conteúdo no corpo)"),
                    @ApiResponse(responseCode = "404", description = "Gerente não encontrado")
            }
    )
    public ResponseEntity<Void> deletarGerente(@PathVariable Long id) {
        service.deletarGerente(id);
        return ResponseEntity.status(204).build();
    }
}
