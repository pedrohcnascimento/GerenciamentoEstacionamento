package inpark.gerenciamentoestacionamento.infraestructure_ui.controller.usuario;

import inpark.gerenciamentoestacionamento.application.dto.requests.CarroRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.CarroResponse;
import inpark.gerenciamentoestacionamento.application.dto.response.ReservaResponse;
import inpark.gerenciamentoestacionamento.application.services.CarroService;
import inpark.gerenciamentoestacionamento.application.services.ReservaService;
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
@RequestMapping("/cliente")
@Tag(
        name = "11. Rotas 1:N — Cliente",
        description = "Operações aninhadas vinculadas a um Cliente (carros e reservas)"
)
public class ClienteRelacionamentoController {
    private final CarroService carroService;
    private final ReservaService reservaService;

    public ClienteRelacionamentoController(CarroService carroService, ReservaService reservaService) {
        this.carroService = carroService;
        this.reservaService = reservaService;
    }

    @GetMapping("/{id}/carros")
    @Operation(
            summary = "Listar carros do cliente (1:N)",
            description = "Requer perfil CLIENTE, DONO, GERENTE ou ADMIN. Retorna os carros ativos vinculados ao cliente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de carros retornada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            }
    )
    public ResponseEntity<List<CarroResponse>> listarCarrosDoCliente(@Parameter(description = "ID do cliente") @PathVariable Long id) {
        return ResponseEntity.ok(carroService.listarPorCliente(id));
    }

    @PostMapping("/{id}/carros")
    @Operation(
            summary = "Cadastrar carro para o cliente (1:N)",
            description = "Requer perfil CLIENTE ou ADMIN. Cadastra um novo carro vinculado automaticamente ao cliente informado na URL.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Carro cadastrado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos (Bean Validation)"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
                    @ApiResponse(responseCode = "409", description = "Placa já cadastrada")
            }
    )
    public ResponseEntity<CarroResponse> cadastrarCarroDoCliente(@Parameter(description = "ID do cliente proprietário do carro") @PathVariable Long id, @RequestBody @Valid CarroRequest dto) {
        CarroRequest req = new CarroRequest(id, dto.placa(), dto.modelo(), dto.cor());
        CarroResponse criado = carroService.cadastrarCarro(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{carroId}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping("/{id}/reservas")
    @Operation(
            summary = "Listar reservas do cliente (1:N)",
            description = "Requer perfil CLIENTE, DONO, GERENTE ou ADMIN. Retorna todas as reservas ativas do cliente informado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de reservas retornada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            }
    )
    public ResponseEntity<List<ReservaResponse>> listarReservasDoCliente(@Parameter(description = "ID do cliente") @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.listarPorCliente(id));
    }
}
