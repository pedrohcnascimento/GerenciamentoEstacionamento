package inpark.gerenciamentoestacionamento.model.entities;

import inpark.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Gerente;
import inpark.gerenciamentoestacionamento.model.enums.Metodo;
import jakarta.persistence.*;
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

@Entity
@Table(name = "estacionamento")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Estacionamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 255)
    private String endereco;

    @Column(nullable = false, length = 20)
    private String CEP;

    @Column(nullable = false, length = 20)
    private String numero;

    private String complemento;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(name = "numero_alvara_de_funcionamento", nullable = false, unique = true, length = 100)
    private String numeroAlvaraDeFuncionamento;

    private Boolean funcionamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dono_id", nullable = false)
    private DonoEstacionamento dono;

    @Builder.Default
    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Gerente> gerentes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Valor> valores = new ArrayList<>();

    private LocalTime horaAbertura;
    private LocalTime horaFechamento;

    @Column(name = "numero_conta_dono", length = 50)
    private String numeroContaDono;

    @Column(name = "valor_arrecadado_do_dia")
    private Double valorArrecadadoDoDia;

    private Double notaMedia;

    @Column(name = "quantidade_de_avaliacoes")
    private Integer quantidadeDeAvaliacoes;

    @Builder.Default
    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reserva> reservas = new ArrayList<>();

    private Double latitude;
    private Double longitude;

    @Column(name = "max_vagas", nullable = false)
    private Integer maxVagas;

    @Column(name = "vagas_disponiveis")
    private Integer vagasDisponiveis;

    @Column(name = "vagas_preferenciais")
    private Integer vagasPreferenciais;

    @Builder.Default
    @Column(name = "margem_seguranca")
    private Integer margemSeguranca = 0;

    private LocalDate diaAtual;

    @Column(name = "numero_de_escritura_imovel", unique = true, length = 100)
    private String numeroDeEscrituraImovel;

    @Column(name = "metodo_de_pagamento")
    @Enumerated(EnumType.STRING)
    private Metodo metodoDePagamento;

    private Boolean status;

    @Builder.Default
    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
