package inpark.gerenciamentoestacionamento.infraestructure_ui.controller.usuario;

import inpark.gerenciamentoestacionamento.application.dto.requests.usuario.ClienteRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.usuario.ClienteResponse;
import inpark.gerenciamentoestacionamento.application.services.usuario.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/cliente")
@Tag(
        name = "2. Clientes",
        description = "Cadastro, consulta, atualização e remoção de clientes"
)
public class ClienteController {
    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    @SecurityRequirements
    @Operation(
            summary = "Listar todos os clientes",
            description = "Acesso público. Retorna todos os clientes ativos cadastrados no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
            }
    )
    public ResponseEntity<List<ClienteResponse>> listarClientes() {
        return ResponseEntity.status(200).body(service.listarClientes());
    }

    @GetMapping("/{id}")
    @SecurityRequirements
    @Operation(
            summary = "Buscar cliente por ID",
            description = "Acesso público. Busca um cliente específico através do seu ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            }
    )
    public ResponseEntity<ClienteResponse> buscarPorId(@Parameter(description = "ID do cliente") @PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PostMapping
    @SecurityRequirements
    @Operation(
            summary = "Cadastrar um novo cliente",
            description = "Acesso público. Cadastra um novo cliente e retorna status 201 com header Location.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos (Bean Validation)"),
                    @ApiResponse(responseCode = "409", description = "Conflito de dados (e-mail já cadastrado)")
            }
    )
    public ResponseEntity<ClienteResponse> cadastrarCliente(@RequestBody @Valid ClienteRequest dto) {
        ClienteResponse criado = service.cadastrarCliente(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar um cliente existente",
            description = "Atualiza os dados de um cliente existente com base no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            }
    )
    public ResponseEntity<ClienteResponse> atualizarCliente(@RequestBody @Valid ClienteRequest dto, @PathVariable Long id) {
        return ResponseEntity.status(200).body(service.atualizarCliente(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar um cliente",
            description = "Desativa (soft delete) um cliente existente com base no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso (sem conteúdo no corpo)"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
                    @ApiResponse(responseCode = "409", description = "Cliente possui registros vinculados")
            }
    )
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        service.deletarCliente(id);
        return ResponseEntity.status(204).build();
    }
}
