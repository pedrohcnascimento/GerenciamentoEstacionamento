CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(50),
    data_nascimento DATE NOT NULL,
    tipo_usuario VARCHAR(50),
    status BOOLEAN DEFAULT TRUE,
    role VARCHAR(50),
    criado_em TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cliente (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES usuario(id)
);

CREATE TABLE IF NOT EXISTS dono_estacionamento (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES usuario(id)
);

CREATE TABLE IF NOT EXISTS estacionamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    cep VARCHAR(20),
    numero VARCHAR(20),
    complemento VARCHAR(255),
    foto_url VARCHAR(500),
    numero_alvara_de_funcionamento VARCHAR(100) NOT NULL,
    funcionamento BOOLEAN DEFAULT TRUE,
    dono_id BIGINT NOT NULL,
    latitude DOUBLE,
    longitude DOUBLE,
    max_vagas INT NOT NULL,
    vagas_disponiveis INT,
    vagas_preferenciais INT,
    margem_seguranca INT DEFAULT 0,
    hora_abertura TIME NOT NULL,
    hora_fechamento TIME NOT NULL,
    metodo_de_pagamento ENUM('PIX', 'DEBITO', 'DINHEIRO') default 'DINHEIRO',
    numero_conta_dono VARCHAR(100),
    dia_atual DATE,
    numero_de_escritura_imovel VARCHAR(100),
    valor_arrecadado_do_dia DECIMAL(10,2) DEFAULT 0,
    nota_media DECIMAL(3,2) DEFAULT 0,
    quantidade_de_avaliacoes INT DEFAULT 0,
    status BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (dono_id) REFERENCES dono_estacionamento(id)
);

CREATE TABLE IF NOT EXISTS gerente (
    id BIGINT PRIMARY KEY,
    cpf_ou_cnpj VARCHAR(18) UNIQUE NOT NULL,
    estacionamento_id BIGINT,
    FOREIGN KEY (id) REFERENCES usuario(id),
    FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id)
);

CREATE TABLE IF NOT EXISTS valor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    preco DECIMAL(10,2) NOT NULL,
    tipo_cobranca ENUM('PORVAGA', 'PORHORA') NOT NULL,
    tipo_pagamento ENUM('PIX', 'DEBITO', 'DINHEIRO') NOT NULL,
    periodo ENUM('UTIL', 'FINALDESEMANA') NOT NULL,
    horario_inicio TIME,
    horario_fim TIME,
    vigencia_inicio DATE,
    vigencia_fim DATE,
    dias_da_semana VARCHAR(100),
    status BOOLEAN DEFAULT TRUE,
    estacionamento_id BIGINT NOT NULL,
    FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id)
);

CREATE TABLE IF NOT EXISTS carro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    placa VARCHAR(10) UNIQUE NOT NULL,
    modelo VARCHAR(255) NOT NULL,
    cor VARCHAR(255) NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE TABLE IF NOT EXISTS plano_estacionamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estacionamento_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(500),
    preco DECIMAL(10,2) NOT NULL,
    modalidade VARCHAR(50),
    vigencia_inicio DATE,
    vigencia_fim DATE,
    horario_inicio TIME,
    horario_fim TIME,
    dias_da_semana VARCHAR(100),
    ativo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id)
);

CREATE TABLE IF NOT EXISTS assinatura (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    plano_id BIGINT NOT NULL,
    inicio_em TIMESTAMP,
    fim_em TIMESTAMP,
    proxima_cobranca_em TIMESTAMP,
    renovacao_automatica BOOLEAN DEFAULT TRUE,
    status VARCHAR(50),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (plano_id) REFERENCES plano_estacionamento(id)
);

CREATE TABLE IF NOT EXISTS reserva (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    estacionamento_id BIGINT NOT NULL,
    veiculo_id BIGINT,
    assinatura_id BIGINT,
    data_reserva DATE,
    hora_reserva TIME,
    inicio_em TIMESTAMP,
    fim_em TIMESTAMP,
    origem VARCHAR(50),
    preco_confirmado DECIMAL(10,2),
    criada_em TIMESTAMP,
    cancelada_em TIMESTAMP,
    status_reserva ENUM('ACEITA', 'RECUSADA', 'PENDENTE', 'ENCERRADA') NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id),
    FOREIGN KEY (veiculo_id) REFERENCES carro(id),
    FOREIGN KEY (assinatura_id) REFERENCES assinatura(id)
);

CREATE TABLE IF NOT EXISTS acesso (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estacionamento_id BIGINT NOT NULL,
    carro_id BIGINT,
    reserva_id BIGINT,
    placa_do_carro VARCHAR(10) NOT NULL,
    hora_de_entrada TIME,
    hora_de_saida TIME,
    total_horas INT,
    valor_a_pagar DECIMAL(10,2),
    status BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id),
    FOREIGN KEY (carro_id) REFERENCES carro(id),
    FOREIGN KEY (reserva_id) REFERENCES reserva(id)
);

CREATE TABLE IF NOT EXISTS avaliacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    estacionamento_id BIGINT NOT NULL,
    nota DECIMAL(3,2) NOT NULL,
    comentario VARCHAR(500),
    data_avaliacao DATETIME NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id)
);

CREATE TABLE IF NOT EXISTS empresa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    razao_social VARCHAR(255) NOT NULL,
    nome_fantasia VARCHAR(255),
    cnpj VARCHAR(18) UNIQUE NOT NULL,
    email_contato VARCHAR(255),
    telefone_contato VARCHAR(50),
    status BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS contrato_empresarial (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    estacionamento_id BIGINT NOT NULL,
    inicio_vigencia DATE,
    fim_vigencia DATE,
    valor_mensal DECIMAL(10,2),
    prazo_pagamento_dias INT,
    status VARCHAR(50),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id),
    FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id)
);

CREATE TABLE IF NOT EXISTS cota_empresarial (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contrato_id BIGINT NOT NULL,
    quantidade_vagas INT NOT NULL,
    dias_da_semana VARCHAR(100),
    horario_inicio TIME,
    horario_fim TIME,
    vigencia_inicio DATE,
    vigencia_fim DATE,
    status VARCHAR(50),
    FOREIGN KEY (contrato_id) REFERENCES contrato_empresarial(id)
);

CREATE TABLE IF NOT EXISTS colaborador_empresa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    documento_hash VARCHAR(255),
    autorizado BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

CREATE TABLE IF NOT EXISTS utilizacao_cota (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cota_id BIGINT NOT NULL,
    colaborador_id BIGINT NOT NULL,
    veiculo_id BIGINT,
    entrada_em TIMESTAMP,
    saida_em TIMESTAMP,
    status BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (cota_id) REFERENCES cota_empresarial(id),
    FOREIGN KEY (colaborador_id) REFERENCES colaborador_empresa(id),
    FOREIGN KEY (veiculo_id) REFERENCES carro(id)
);

CREATE TABLE IF NOT EXISTS bloqueio_capacidade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estacionamento_id BIGINT NOT NULL,
    quantidade_vagas INT NOT NULL,
    inicio_em TIMESTAMP,
    fim_em TIMESTAMP,
    motivo VARCHAR(255),
    status VARCHAR(50),
    FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id)
);

CREATE TABLE IF NOT EXISTS pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pagador_id BIGINT,
    reserva_id BIGINT,
    assinatura_id BIGINT,
    contrato_id BIGINT,
    valor DECIMAL(10,2) NOT NULL,
    metodo VARCHAR(50),
    provedor VARCHAR(100),
    transacao_externa_id VARCHAR(255),
    status VARCHAR(50),
    criado_em TIMESTAMP,
    confirmado_em TIMESTAMP,
    FOREIGN KEY (pagador_id) REFERENCES cliente(id),
    FOREIGN KEY (reserva_id) REFERENCES reserva(id),
    FOREIGN KEY (assinatura_id) REFERENCES assinatura(id),
    FOREIGN KEY (contrato_id) REFERENCES contrato_empresarial(id)
);

CREATE TABLE IF NOT EXISTS relatorio_financeiro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estacionamento_id BIGINT NOT NULL,
    nome_arquivo VARCHAR(255),
    formato VARCHAR(50),
    periodo_inicio DATE,
    periodo_fim DATE,
    importado_em TIMESTAMP,
    status VARCHAR(50),
    FOREIGN KEY (estacionamento_id) REFERENCES estacionamento(id)
);

CREATE TABLE IF NOT EXISTS divergencia_financeira (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    relatorio_id BIGINT NOT NULL,
    tipo VARCHAR(50),
    descricao VARCHAR(500),
    valor_esperado DECIMAL(10,2),
    valor_identificado DECIMAL(10,2),
    diferenca DECIMAL(10,2),
    status VARCHAR(50),
    observacao_analista VARCHAR(500),
    FOREIGN KEY (relatorio_id) REFERENCES relatorio_financeiro(id)
);
