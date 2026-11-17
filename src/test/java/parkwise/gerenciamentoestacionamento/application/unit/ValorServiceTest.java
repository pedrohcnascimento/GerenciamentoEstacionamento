package parkwise.gerenciamentoestacionamento.application.unit;

import parkwise.gerenciamentoestacionamento.application.dto.requests.ValorRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.ValorResponse;
import parkwise.gerenciamentoestacionamento.application.services.ValorService;
import parkwise.gerenciamentoestacionamento.dao.EstacionamentoDao;
import parkwise.gerenciamentoestacionamento.dao.ValorDao;
import parkwise.gerenciamentoestacionamento.model.entities.Estacionamento;
import parkwise.gerenciamentoestacionamento.model.entities.Valor;
import parkwise.gerenciamentoestacionamento.model.enums.Cobranca;
import parkwise.gerenciamentoestacionamento.model.enums.Metodo;
import parkwise.gerenciamentoestacionamento.model.enums.Periodo;
import parkwise.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
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
    private ValorDao ValorDao;
    @Mock
    private EstacionamentoDao EstacionamentoDao;
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
        when(ValorDao.findByStatusTrue()).thenReturn(List.of(valor));
        List<ValorResponse> lista = valorService.listarValor();
        assertEquals(1, lista.size());
        assertEquals(10.0, lista.get(0).preco());
        verify(ValorDao).findByStatusTrue();
    }

    @Test
    void deveBuscarValorPorId() {
        when(ValorDao.findById(1L)).thenReturn(Optional.of(valor));
        ValorResponse response = valorService.buscarPorId(1L);
        assertEquals(1L, response.id());
        verify(ValorDao).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarIdNaoCadastrado() {
        when(ValorDao.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> valorService.buscarPorId(2L));
        assertEquals("ID buscado não foi encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveCadastrarValor() {
        when(EstacionamentoDao.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(ValorDao.save(any(Valor.class))).thenReturn(valor);

        ValorResponse response = valorService.cadastrarValor(valorRequest);
        assertEquals(10.0, response.preco());
        assertTrue(response.status());
        verify(ValorDao).save(any(Valor.class));
    }

    @Test
    void deveLancarExcecaoAoCadastrarComEstacionamentoInexistente() {
        when(EstacionamentoDao.findById(2L)).thenReturn(Optional.empty());
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

        when(ValorDao.findById(1L)).thenReturn(Optional.of(valor));
        when(EstacionamentoDao.findById(2L)).thenReturn(Optional.of(novoEstacionamento));
        when(ValorDao.save(any(Valor.class))).thenReturn(valor);

        ValorResponse response = valorService.atualizarValor(req, 1L);
        assertEquals(Cobranca.PORVAGA, response.tipoDeCobranca());
        assertEquals(Metodo.PIX, response.tipoDePagamento());
        assertEquals(20.0, response.preco());
        assertEquals(Periodo.FINALDESEMANA, response.periodo());
        verify(ValorDao).save(any(Valor.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarValorOuEstacionamentoInexistente() {
        when(ValorDao.findById(2L)).thenReturn(Optional.empty());
        when(EstacionamentoDao.findById(1L)).thenReturn(Optional.of(estacionamento));
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> valorService.atualizarValor(valorRequest, 2L));
        assertEquals("ID de valor ou estacionamento buscados não encontrados", ex.getMessage());
    }

    @Test
    void deveDeletarValor() {
        when(ValorDao.findById(1L)).thenReturn(Optional.of(valor));
        when(ValorDao.save(any(Valor.class))).thenReturn(valor);

        valorService.deletarValor(1L);

        assertFalse(valor.getStatus());
        verify(ValorDao).save(valor);
    }

    @Test
    void deveLancarExcecaoAoDeletarValorInexistente() {
        when(ValorDao.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> valorService.deletarValor(2L));
        assertEquals("Id de valor especificado não encontrado no sistema", ex.getMessage());
    }
}
