package inpark.gerenciamentoestacionamento.model.entities;

import inpark.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Gerente;
import inpark.gerenciamentoestacionamento.model.enums.Metodo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Estacionamento {
    private Long id;
    private String nome;
    private String endereco;
    private String CEP;
    private String numero;
    private String complemento;
    private String fotoUrl;
    private String numeroAlvaraDeFuncionamento;
    private Boolean funcionamento;
    private DonoEstacionamento dono;

    @Builder.Default
    private List<Gerente> gerentes = new ArrayList<>();

    @Builder.Default
    private List<Valor> valores = new ArrayList<>();

    private LocalTime horaAbertura;
    private LocalTime horaFechamento;
    private String numeroContaDono;
    private Double valorArrecadadoDoDia;
    private Double notaMedia;
    private Integer quantidadeDeAvaliacoes;

    @Builder.Default
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    @Builder.Default
    private List<Reserva> reservas = new ArrayList<>();

    private Double latitude;
    private Double longitude;
    private Integer maxVagas;
    private Integer vagasDisponiveis;
    private Integer vagasPreferenciais;

    @Builder.Default
    private Integer margemSeguranca = 0;

    private LocalDate diaAtual;
    private String numeroDeEscrituraImovel;
    private Metodo metodoDePagamento;
    private Boolean status;

    @Builder.Default
    private List<Acesso> acessos = new ArrayList<>();

    public int calcularCapacidadeOperacional() {
        int total = maxVagas != null ? maxVagas : 0;
        int margem = margemSeguranca != null ? margemSeguranca : 0;
        return Math.max(0, total - margem);
    }

    public void calcularNotaMedia() {
        if (avaliacoes == null || avaliacoes.isEmpty()) {
            this.quantidadeDeAvaliacoes = 0;
            this.notaMedia = 0.0;
            return;
        }

        List<Avaliacao> ativas = avaliacoes.stream()
                .filter(a -> a != null && a.getNota() != null && !Boolean.FALSE.equals(a.getStatus()))
                .toList();

        if (ativas.isEmpty()) {
            this.quantidadeDeAvaliacoes = 0;
            this.notaMedia = 0.0;
            return;
        }

        this.quantidadeDeAvaliacoes = ativas.size();
        double soma = 0.0;
        for (Avaliacao avaliacao : ativas) {
            soma += avaliacao.getNota().doubleValue();
        }
        notaMedia = soma / this.quantidadeDeAvaliacoes;
    }
}
