package inpark.gerenciamentoestacionamento.infraestructure_ui.controller;

import inpark.gerenciamentoestacionamento.application.dto.requests.ReservaRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.ReservaResponse;
import inpark.gerenciamentoestacionamento.application.services.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reserva")
@Tag(
        name = "Reserva Controller",
        description = "Realiza operações referentes a criação de reservas"
)
public class ReservaController {
    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            method = "GET",
            summary = "Listar reservas",
            description = "Cria uma lista de todas as reservas ativas presentes no sistema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de reservas retornada com sucesso")
            }
    )
    public ResponseEntity<List<ReservaResponse>> listarReservas() {
        return ResponseEntity.status(200).body(service.listarReservas());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar por ID",
            description = "Busca uma reserva em específico através do ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reserva encontrada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
            }
    )
    public ResponseEntity<ReservaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(
            method = "POST",
            summary = "Cadastrar reserva",
            description = "Cadastra uma nova reserva no sistema",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Reserva cadastrada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "404", description = "Cliente ou estacionamento não encontrado")
            }
    )
    public ResponseEntity<ReservaResponse> cadastrarReserva(@RequestBody ReservaRequest dto) {
        return ResponseEntity.status(201).body(service.cadastrarReserva(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            method = "PUT",
            summary = "Atualizar reserva",
            description = "Altera as informações de uma reserva",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reserva atualizada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
            }
    )
    public ResponseEntity<ReservaResponse> atualizarReserva(@RequestBody ReservaRequest dto, @PathVariable Long id) {
        return ResponseEntity.status(200).body(service.atualizarReserva(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            method = "DELETE",
            summary = "Deletar reserva",
            description = "Desativa (soft delete) uma reserva do sistema",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Reserva desativada com sucesso (sem conteúdo no corpo)"),
                    @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
            }
    )
    public ResponseEntity<Void> deletarReserva(@PathVariable Long id) {
        service.deletarReserva(id);
        return ResponseEntity.status(204).build();
    }
}
