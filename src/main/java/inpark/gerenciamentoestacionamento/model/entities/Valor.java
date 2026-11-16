package inpark.gerenciamentoestacionamento.model.entities;

import inpark.gerenciamentoestacionamento.model.enums.Cobranca;
import inpark.gerenciamentoestacionamento.model.enums.Metodo;
import inpark.gerenciamentoestacionamento.model.enums.Periodo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Valor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_cobranca")
    @Enumerated(EnumType.STRING)
    private Cobranca tipoDeCobranca;

    @Column(name = "tipo_pagamento")
    @Enumerated(EnumType.STRING)
    private Metodo tipoDePagamento;

    private Double preco;

    @Column(name = "periodo")
    @Enumerated(EnumType.STRING)
    private Periodo periodo;

    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private LocalDate vigenciaInicio;
    private LocalDate vigenciaFim;
    private String diasDaSemana;

    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "estacionamento_id")
    private Estacionamento estacionamento;
}
