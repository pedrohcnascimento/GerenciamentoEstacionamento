package parkwise.gerenciamentoestacionamento.application.unit;

import parkwise.gerenciamentoestacionamento.application.dto.requests.AcessoRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.AcessoResponse;
import parkwise.gerenciamentoestacionamento.application.services.AcessoService;
import parkwise.gerenciamentoestacionamento.dao.AcessoDao;
import parkwise.gerenciamentoestacionamento.dao.CarroDao;
import parkwise.gerenciamentoestacionamento.dao.EstacionamentoDao;
import parkwise.gerenciamentoestacionamento.model.entities.Acesso;
import parkwise.gerenciamentoestacionamento.model.entities.Estacionamento;
import parkwise.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AcessoServiceTest {

    @Mock
    private AcessoDao AcessoDao;
    @Mock
    private EstacionamentoDao EstacionamentoDao;
    @Mock
    private CarroDao CarroDao;
    @InjectMocks
    private AcessoService acessoService;

    private Estacionamento estacionamento;
    private Acesso acesso;
    private AcessoRequest acessoRequest;

    @BeforeEach
    void setUp() {
        estacionamento = new Estacionamento();
        estacionamento.setId(1L);
        estacionamento.setAcessos(new ArrayList<>());

        acesso = new Acesso();
        acesso.setId(1L);
        acesso.setPlacaDoCarro("ABC1234");
        acesso.setHoraDeEntrada(LocalTime.parse("08:00:00"));
        acesso.setHoraDeSaida(LocalTime.parse("10:00:00"));
        acesso.setValorAPagar(20.0);
        acesso.setStatus(true);
        acesso.setEstacionamento(estacionamento);

        acessoRequest = new AcessoRequest(
                1L,
                "ABC1234",
                LocalTime.parse("08:00:00"),
                LocalTime.parse("10:00:00"),
                20.0,
                estacionamento.getId()
        );
    }

    @Test
    void deveListarAcessos() {
        when(AcessoDao.findByStatusTrue()).thenReturn(List.of(acesso));
        List<AcessoResponse> lista = acessoService.listarAcessos();
        assertEquals(1, lista.size());
        assertEquals("ABC1234", lista.get(0).placaDoCarro());
        verify(AcessoDao).findByStatusTrue();
    }

    @Test
    void deveBuscarAcessoPorId() {
        when(AcessoDao.findById(1L)).thenReturn(Optional.of(acesso));
        AcessoResponse response = acessoService.buscarPorId(1L);
        assertEquals("ABC1234", response.placaDoCarro());
        verify(AcessoDao).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarIdNaoCadastrado() {
        when(AcessoDao.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> acessoService.buscarPorId(2L));
        assertEquals("ID buscado não foi encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveCadastrarAcesso() {
        when(EstacionamentoDao.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(AcessoDao.save(any(Acesso.class))).thenReturn(acesso);

        AcessoResponse response = acessoService.cadastrarAcesso(acessoRequest);
        assertEquals("ABC1234", response.placaDoCarro());
        assertTrue(response.status());
        verify(AcessoDao).save(any(Acesso.class));
    }

    @Test
    void deveLancarExcecaoAoCadastrarComEstacionamentoInexistente() {
        when(EstacionamentoDao.findById(2L)).thenReturn(Optional.empty());
        AcessoRequest req = new AcessoRequest(
                1L, "ABC1234", LocalTime.parse("08:00:00"), LocalTime.parse("10:00:00"), 20.0, 2L
        );
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> acessoService.cadastrarAcesso(req));
        assertEquals("Id do estacionamento não encontrado no sistema", ex.getMessage());
    }

    @Test
    void deveAtualizarAcesso() {
        when(AcessoDao.findById(1L)).thenReturn(Optional.of(acesso));
        when(EstacionamentoDao.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(AcessoDao.save(any(Acesso.class))).thenReturn(acesso);

        AcessoResponse response = acessoService.atualizarAcesso(acessoRequest, 1L);
        assertEquals("ABC1234", response.placaDoCarro());
        verify(AcessoDao).save(any(Acesso.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarAcessoOuEstacionamentoInexistente() {
        when(AcessoDao.findById(2L)).thenReturn(Optional.empty());
        when(EstacionamentoDao.findById(1L)).thenReturn(Optional.of(estacionamento));
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> acessoService.atualizarAcesso(acessoRequest, 2L));
        assertEquals("O Acesso ou estacionamento buscado não existe no sistema", ex.getMessage());
    }

    @Test
    void deveDeletarAcesso() {
        estacionamento.getAcessos().add(acesso);
        when(AcessoDao.findById(1L)).thenReturn(Optional.of(acesso));
        when(AcessoDao.save(any(Acesso.class))).thenReturn(acesso);

        acessoService.deletarAcesso(1L);

        assertFalse(acesso.getStatus());
        assertFalse(estacionamento.getAcessos().contains(acesso));
        verify(AcessoDao).save(acesso);
    }

    @Test
    void deveLancarExcecaoAoDeletarAcessoInexistente() {
        when(AcessoDao.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> acessoService.deletarAcesso(2L));
        assertEquals("Acesso buscado não cadastrado no sistema", ex.getMessage());
    }
}
