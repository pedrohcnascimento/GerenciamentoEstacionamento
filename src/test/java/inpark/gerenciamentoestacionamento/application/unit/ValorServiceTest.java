package inpark.gerenciamentoestacionamento.application.unit;

import inpark.gerenciamentoestacionamento.application.dto.requests.ValorRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.ValorResponse;
import inpark.gerenciamentoestacionamento.application.services.ValorService;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.EstacionamentoRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.ValorRepository;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.Valor;
import inpark.gerenciamentoestacionamento.model.enums.Cobranca;
import inpark.gerenciamentoestacionamento.model.enums.Metodo;
import inpark.gerenciamentoestacionamento.model.enums.Periodo;
import inpark.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ValorServiceTest {

    @Mock
    private ValorRepository valorRepository;
    @Mock
    private EstacionamentoRepository estacionamentoRepository;
    @InjectMocks
    private ValorService valorService;

    private Estacionamento estacionamento;
    private Valor valor;
    private ValorRequest valorRequest;

    @BeforeEach
    void setUp() {
        estacionamento = new Estacionamento();
        estacionamento.setId(1L);
        estacionamento.setValores(new ArrayList<>());

        valor = new Valor();
        valor.setId(1L);
        valor.setTipoDeCobranca(Cobranca.PORHORA);
        valor.setTipoDePagamento(Metodo.DINHEIRO);
        valor.setPreco(10.0);
        valor.setPeriodo(Periodo.FINALDESEMANA);
        valor.setEstacionamento(estacionamento);
        valor.setStatus(true);

        valorRequest = new ValorRequest(
                Cobranca.PORHORA,
                Metodo.DINHEIRO,
                10.0,
                Periodo.FINALDESEMANA,
                1L
        );
    }

    @Test
    void deveListarValores() {
        when(valorRepository.findByStatusTrue()).thenReturn(List.of(valor));
        List<ValorResponse> lista = valorService.listarValor();
        assertEquals(1, lista.size());
        assertEquals(10.0, lista.get(0).preco());
        verify(valorRepository).findByStatusTrue();
    }

    @Test
    void deveBuscarValorPorId() {
        when(valorRepository.findById(1L)).thenReturn(Optional.of(valor));
        ValorResponse response = valorService.buscarPorId(1L);
        assertEquals(1L, response.id());
        verify(valorRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarIdNaoCadastrado() {
        when(valorRepository.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> valorService.buscarPorId(2L));
        assertEquals("ID buscado não foi encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveCadastrarValor() {
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(valorRepository.save(any(Valor.class))).thenReturn(valor);

        ValorResponse response = valorService.cadastrarValor(valorRequest);
        assertEquals(10.0, response.preco());
        assertTrue(response.status());
        verify(valorRepository).save(any(Valor.class));
    }

    @Test
    void deveLancarExcecaoAoCadastrarComEstacionamentoInexistente() {
        when(estacionamentoRepository.findById(2L)).thenReturn(Optional.empty());
        ValorRequest req = new ValorRequest(Cobranca.PORHORA, Metodo.DINHEIRO, 10.0, Periodo.FINALDESEMANA, 2L);
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> valorService.cadastrarValor(req));
        assertEquals("Id de estacionamento especificado não encontrado no sistema", ex.getMessage());
    }

    @Test
    void deveAtualizarValor() {
        Estacionamento novoEstacionamento = new Estacionamento();
        novoEstacionamento.setId(2L);
        novoEstacionamento.setValores(new ArrayList<>());

        ValorRequest req = new ValorRequest(Cobranca.PORVAGA, Metodo.PIX, 20.0, Periodo.FINALDESEMANA, 2L);

        when(valorRepository.findById(1L)).thenReturn(Optional.of(valor));
        when(estacionamentoRepository.findById(2L)).thenReturn(Optional.of(novoEstacionamento));
        when(valorRepository.save(any(Valor.class))).thenReturn(valor);

        ValorResponse response = valorService.atualizarValor(req, 1L);
        assertEquals(Cobranca.PORVAGA, response.tipoDeCobranca());
        assertEquals(Metodo.PIX, response.tipoDePagamento());
        assertEquals(20.0, response.preco());
        assertEquals(Periodo.FINALDESEMANA, response.periodo());
        verify(valorRepository).save(any(Valor.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarValorOuEstacionamentoInexistente() {
        when(valorRepository.findById(2L)).thenReturn(Optional.empty());
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> valorService.atualizarValor(valorRequest, 2L));
        assertEquals("ID de valor ou estacionamento buscados não encontrados", ex.getMessage());
    }

    @Test
    void deveDeletarValor() {
        when(valorRepository.findById(1L)).thenReturn(Optional.of(valor));
        when(valorRepository.save(any(Valor.class))).thenReturn(valor);

        valorService.deletarValor(1L);

        assertFalse(valor.getStatus());
        verify(valorRepository).save(valor);
    }

    @Test
    void deveLancarExcecaoAoDeletarValorInexistente() {
        when(valorRepository.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> valorService.deletarValor(2L));
        assertEquals("Id de valor especificado não encontrado no sistema", ex.getMessage());
    }
}
