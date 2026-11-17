-- =====================================================================
-- SCRIPT DDL ORACLE FIAP - PADRAO DA DISCIPLINA (tb_, sq_tb_, cd_, nm_, ds_, dt_, vl_, nr_, st_)
-- =====================================================================

CREATE SEQUENCE sq_tb_usuario START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_tb_estacionamento START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_tb_valor START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_tb_carro START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_tb_reserva START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_tb_acesso START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_tb_avaliacao START WITH 1 INCREMENT BY 1;

CREATE TABLE tb_usuario (
    cd_usuario NUMBER(19) PRIMARY KEY,
    nm_usuario VARCHAR2(255) NOT NULL,
    ds_email VARCHAR2(255) NOT NULL UNIQUE,
    ds_senha VARCHAR2(255) NOT NULL,
    nr_telefone VARCHAR2(50),
    dt_nascimento DATE NOT NULL,
    tp_usuario VARCHAR2(50),
    st_ativo NUMBER(1) DEFAULT 1,
    ds_role VARCHAR2(50),
    dt_criado_em TIMESTAMP
);

CREATE TABLE tb_cliente (
    cd_cliente NUMBER(19) PRIMARY KEY,
    CONSTRAINT fk_tb_cliente_usuario FOREIGN KEY (cd_cliente) REFERENCES tb_usuario(cd_usuario)
);

CREATE TABLE tb_dono_estacionamento (
    cd_dono NUMBER(19) PRIMARY KEY,
    CONSTRAINT fk_tb_dono_usuario FOREIGN KEY (cd_dono) REFERENCES tb_usuario(cd_usuario)
);

CREATE TABLE tb_estacionamento (
    cd_estacionamento NUMBER(19) PRIMARY KEY,
    cd_dono NUMBER(19) NOT NULL,
    nm_estacionamento VARCHAR2(100) NOT NULL,
    ds_endereco VARCHAR2(255) NOT NULL,
    nr_cep VARCHAR2(20),
    nr_endereco VARCHAR2(20),
    ds_complemento VARCHAR2(255),
    ds_foto_url VARCHAR2(500),
    nr_alvara_funcionamento VARCHAR2(100) NOT NULL UNIQUE,
    st_funcionamento NUMBER(1) DEFAULT 1,
    vl_latitude NUMBER(10,6),
    vl_longitude NUMBER(10,6),
    nr_max_vagas NUMBER(10) NOT NULL,
    nr_vagas_disponiveis NUMBER(10),
    nr_vagas_preferenciais NUMBER(10),
    nr_margem_seguranca NUMBER(10) DEFAULT 0,
    hr_abertura VARCHAR2(15) NOT NULL,
    hr_fechamento VARCHAR2(15) NOT NULL,
    tp_metodo_pagamento VARCHAR2(50) DEFAULT 'DINHEIRO',
    nr_conta_dono VARCHAR2(100),
    dt_atual DATE,
    nr_escritura_imovel VARCHAR2(100),
    vl_arrecadado_dia NUMBER(10,2) DEFAULT 0,
    vl_nota_media NUMBER(4,2) DEFAULT 0,
    nr_quantidade_avaliacoes NUMBER(10) DEFAULT 0,
    st_ativo NUMBER(1) DEFAULT 1,
    CONSTRAINT fk_tb_estac_dono FOREIGN KEY (cd_dono) REFERENCES tb_dono_estacionamento(cd_dono)
);

CREATE TABLE tb_gerente (
    cd_gerente NUMBER(19) PRIMARY KEY,
    nr_cpf_cnpj VARCHAR2(18) UNIQUE NOT NULL,
    cd_estacionamento NUMBER(19),
    CONSTRAINT fk_tb_gerente_usuario FOREIGN KEY (cd_gerente) REFERENCES tb_usuario(cd_usuario),
    CONSTRAINT fk_tb_gerente_estac FOREIGN KEY (cd_estacionamento) REFERENCES tb_estacionamento(cd_estacionamento)
);

CREATE TABLE tb_valor (
    cd_valor NUMBER(19) PRIMARY KEY,
    cd_estacionamento NUMBER(19) NOT NULL,
    vl_preco NUMBER(10,2) NOT NULL,
    tp_cobranca VARCHAR2(50) NOT NULL,
    tp_pagamento VARCHAR2(50) NOT NULL,
    tp_periodo VARCHAR2(50) NOT NULL,
    st_ativo NUMBER(1) DEFAULT 1,
    CONSTRAINT fk_tb_valor_estac FOREIGN KEY (cd_estacionamento) REFERENCES tb_estacionamento(cd_estacionamento)
);

CREATE TABLE tb_carro (
    cd_carro NUMBER(19) PRIMARY KEY,
    cd_cliente NUMBER(19) NOT NULL,
    ds_placa VARCHAR2(10) UNIQUE NOT NULL,
    nm_modelo VARCHAR2(255) NOT NULL,
    nm_cor VARCHAR2(255) NOT NULL,
    st_ativo NUMBER(1) DEFAULT 1,
    CONSTRAINT fk_tb_carro_cliente FOREIGN KEY (cd_cliente) REFERENCES tb_cliente(cd_cliente)
);

CREATE TABLE tb_reserva (
    cd_reserva NUMBER(19) PRIMARY KEY,
    cd_cliente NUMBER(19) NOT NULL,
    cd_estacionamento NUMBER(19) NOT NULL,
    dt_reserva DATE,
    hr_reserva VARCHAR2(15),
    st_reserva VARCHAR2(50) NOT NULL,
    st_ativo NUMBER(1) DEFAULT 1,
    CONSTRAINT fk_tb_reserva_cliente FOREIGN KEY (cd_cliente) REFERENCES tb_cliente(cd_cliente),
    CONSTRAINT fk_tb_reserva_estac FOREIGN KEY (cd_estacionamento) REFERENCES tb_estacionamento(cd_estacionamento)
);

CREATE TABLE tb_acesso (
    cd_acesso NUMBER(19) PRIMARY KEY,
    cd_estacionamento NUMBER(19) NOT NULL,
    cd_carro NUMBER(19),
    ds_placa_carro VARCHAR2(10) NOT NULL,
    hr_entrada VARCHAR2(15),
    hr_saida VARCHAR2(15),
    nr_total_horas NUMBER(10),
    vl_pagar NUMBER(10,2),
    st_ativo NUMBER(1) DEFAULT 1,
    CONSTRAINT fk_tb_acesso_estac FOREIGN KEY (cd_estacionamento) REFERENCES tb_estacionamento(cd_estacionamento),
    CONSTRAINT fk_tb_acesso_carro FOREIGN KEY (cd_carro) REFERENCES tb_carro(cd_carro)
);

CREATE TABLE tb_avaliacao (
    cd_avaliacao NUMBER(19) PRIMARY KEY,
    cd_cliente NUMBER(19) NOT NULL,
    cd_estacionamento NUMBER(19) NOT NULL,
    nr_nota NUMBER(3) NOT NULL,
    ds_comentario VARCHAR2(500),
    dt_avaliacao TIMESTAMP NOT NULL,
    st_ativo NUMBER(1) DEFAULT 1,
    CONSTRAINT fk_tb_aval_cliente FOREIGN KEY (cd_cliente) REFERENCES tb_cliente(cd_cliente),
    CONSTRAINT fk_tb_aval_estac FOREIGN KEY (cd_estacionamento) REFERENCES tb_estacionamento(cd_estacionamento)
);
