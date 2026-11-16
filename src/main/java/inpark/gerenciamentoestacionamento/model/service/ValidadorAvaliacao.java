package inpark.gerenciamentoestacionamento.model.service;

import inpark.gerenciamentoestacionamento.infraestructure.repositories.AvaliacaoRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.EstacionamentoRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.usuario.ClienteRepository;
import inpark.gerenciamentoestacionamento.model.entities.Avaliacao;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import inpark.gerenciamentoestacionamento.model.exceptions.AvaliacaoInvalida;
import inpark.gerenciamentoestacionamento.model.exceptions.ComentarioMuitoLongo;
import inpark.gerenciamentoestacionamento.model.exceptions.TempoLimiteDeAvaliacaoExpedido;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ValidadorAvaliacao {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ClienteRepository clienteRepository;
    private final EstacionamentoRepository estacionamentoRepository;

    public ValidadorAvaliacao(AvaliacaoRepository avaliacaoRepository,
                              ClienteRepository clienteRepository,
                              EstacionamentoRepository estacionamentoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.clienteRepository = clienteRepository;
        this.estacionamentoRepository = estacionamentoRepository;
    }

    private boolean mesmoEstacionamento(Estacionamento a, Estacionamento b) {
        if (a == null || b == null) return false;
        if (a.equals(b)) return true;
        return a.getId() != null && a.getId().equals(b.getId());
    }

    public void validarAvaliacaoAposUso(Avaliacao avaliacao) {
        if (avaliacao.getNota() != null && (avaliacao.getNota() < 0 || avaliacao.getNota() > 5)) {
            throw new AvaliacaoInvalida("A nota deve estar entre 0 e 5");
        }

        Cliente cliente = avaliacao.getCliente();
        Estacionamento estacionamento = avaliacao.getEstacionamento();

        boolean temReserva = cliente.getReservas() != null && !cliente.getReservas()
                .stream()
                .filter(reserva -> mesmoEstacionamento(reserva.getEstacionamento(), estacionamento))
                .toList()
                .isEmpty();

        boolean temAcesso = cliente.getCarros() != null && !cliente.getCarros()
                .stream()
                .filter(carro -> carro.getAcessos() != null && carro.getAcessos()
                        .stream()
                        .anyMatch(acesso -> mesmoEstacionamento(acesso.getEstacionamento(), estacionamento)))
                .toList()
                .isEmpty();

        if (!temReserva && !temAcesso) {
            throw new AvaliacaoInvalida("Cliente não possui reserva ou acessos registrados neste estacionamento nesse estacionamento");
        }
    }

    public void validarTempoDeAvaliacao(Avaliacao avaliacao) {
        LocalDateTime dataInical = avaliacao.getDataDeAvaliacao();
        if (dataInical == null) {
            return;
        }
        LocalDateTime dataAtual = LocalDateTime.now();

        if (ChronoUnit.DAYS.between(dataInical, dataAtual) >= 7) {
            throw new TempoLimiteDeAvaliacaoExpedido("Expedido tempo limite de 7 dias para alterar a avaliação requisitada");
        }
    }

    public void validarTamanhoDoComentario(Avaliacao avaliacao) {
        if (avaliacao.getComentario() != null && avaliacao.getComentario().length() > 500) {
            throw new ComentarioMuitoLongo("Comentario muito longo");
        }
    }
}
