package inpark.gerenciamentoestacionamento.infraestructure_ui.handlers;

import inpark.gerenciamentoestacionamento.application.dto.requests.AcessoRequest;
import inpark.gerenciamentoestacionamento.application.services.AcessoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AssinanteHandler {
    private final AcessoService acessoService;

    public void receberMensagem(AcessoRequest dto) {
        acessoService.cadastrarAcesso(dto);
    }
}
