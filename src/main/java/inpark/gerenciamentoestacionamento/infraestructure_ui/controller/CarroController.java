package inpark.gerenciamentoestacionamento.infraestructure_ui.controller;

import inpark.gerenciamentoestacionamento.application.dto.requests.CarroRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.CarroResponse;
import inpark.gerenciamentoestacionamento.application.services.CarroService;
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
@RequestMapping("/carro")
@Tag(
        name = "06. Carros",
        description = "CRUD direto de veículos (requer perfil CLIENTE ou ADMIN)"
)
public class CarroController {
    private final CarroService service;

    public CarroController(CarroService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Listar carros",
            description = "Lista todos os carros ativos cadastrados no sistema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de carros retornada com sucesso")
            }
    )
    public ResponseEntity<List<CarroResponse>> listarCarros() {
        return ResponseEntity.status(200).body(service.listarCarros());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar por ID",
            description = "Busca um carro em específico através do ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Carro encontrado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Carro não encontrado")
            }
    )
    public ResponseEntity<CarroResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar carro",
            description = "Atualiza os dados de um carro cadastrado no sistema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Carro atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "404", description = "Carro ou cliente não encontrado")
            }
    )
    public ResponseEntity<CarroResponse> atualizarCarro(@RequestBody @Valid CarroRequest dto, @PathVariable Long id) {
        return ResponseEntity.status(200).body(service.atualizarCarro(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar carro",
            description = "Desativa (soft delete) um carro cadastrado no sistema",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Carro desativado com sucesso (sem conteúdo no corpo)"),
                    @ApiResponse(responseCode = "404", description = "Carro não encontrado")
            }
    )
    public ResponseEntity<Void> deletarCarro(@PathVariable Long id) {
        service.deletarCarro(id);
        return ResponseEntity.status(204).build();
    }
}
