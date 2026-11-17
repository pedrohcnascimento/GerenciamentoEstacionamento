package inpark.gerenciamentoestacionamento.application.unit;

import inpark.gerenciamentoestacionamento.application.dto.requests.ReservaRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.ReservaResponse;
import inpark.gerenciamentoestacionamento.application.services.ReservaService;
import inpark.gerenciamentoestacionamento.dao.EstacionamentoDao;
import inpark.gerenciamentoestacionamento.dao.ReservaDao;
import inpark.gerenciamentoestacionamento.dao.ClienteDao;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.Reserva;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import inpark.gerenciamentoestacionamento.model.enums.StatusReserva;
import inpark.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock
    private ReservaDao ReservaDao;
    @Mock
    private EstacionamentoDao EstacionamentoDao;
    @Mock
    private ClienteDao ClienteDao;
    @InjectMocks
    private ReservaService reservaService;

    private Cliente cliente;
    private Estacionamento estacionamento;
    private Reserva reserva;
    private ReservaRequest reservaRequest;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setReservas(new ArrayList<>());

        estacionamento = new Estacionamento();
        estacionamento.setId(1L);
        estacionamento.setReservas(new ArrayList<>());
        estacionamento.setAvaliacoes(new ArrayList<>());
        estacionamento.setAcessos(new ArrayList<>());
        estacionamento.setGerentes(new ArrayList<>());

        reserva = new Reserva();
        reserva.setId(1L);
        reserva.setCliente(cliente);
        reserva.setEstacionamento(estacionamento);
        reserva.setDataDaReserva(new Date());
        reserva.setHoraDaReserva(Time.valueOf("14:30:00"));
        reserva.setStatusReserva(StatusReserva.PENDENTE);
        reserva.setStatus(true);

        reservaRequest = new ReservaRequest(
                1L,
                1L,
                new Date(),
                Time.valueOf("14:30:00"),
                StatusReserva.PENDENTE
        );
    }

    @Test
    void deveListarReservas() {
        when(ReservaDao.findByStatusTrue()).thenReturn(List.of(reserva));
        List<ReservaResponse> lista = reservaService.listarReservas();
        assertEquals(1, lista.size());
        assertEquals(1L, lista.get(0).id());
        verify(ReservaDao).findByStatusTrue();
    }

    @Test
    void deveBuscarReservaPorId() {
        when(ReservaDao.findById(1L)).thenReturn(Optional.of(reserva));
        ReservaResponse response = reservaService.buscarPorId(1L);
        assertEquals(1L, response.id());
        verify(ReservaDao).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarIdNaoCadastrado() {
        when(ReservaDao.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> reservaService.buscarPorId(2L));
        assertEquals("ID buscado não foi encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveCadastrarReserva() {
        when(ClienteDao.findById(1L)).thenReturn(Optional.of(cliente));
        when(EstacionamentoDao.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(ReservaDao.save(any(Reserva.class))).thenReturn(reserva);

        ReservaResponse response = reservaService.cadastrarReserva(reservaRequest);
        assertEquals(1L, response.id());
        assertTrue(response.status());
        verify(ReservaDao).save(any(Reserva.class));
    }

    @Test
    void deveLancarExcecaoAoCadastrarComClienteOuEstacionamentoInexistente() {
        when(ClienteDao.findById(2L)).thenReturn(Optional.empty());
        when(EstacionamentoDao.findById(1L)).thenReturn(Optional.of(estacionamento));
        ReservaRequest req = new ReservaRequest(2L, 1L, new Date(), Time.valueOf("14:30:00"), StatusReserva.PENDENTE);
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> reservaService.cadastrarReserva(req));
        assertEquals("Cliente ou estacionameno não encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveAtualizarReserva() {
        when(ReservaDao.findById(1L)).thenReturn(Optional.of(reserva));
        when(ReservaDao.save(any(Reserva.class))).thenReturn(reserva);

        ReservaRequest req = new ReservaRequest(1L, 1L, new Date(), Time.valueOf("15:00:00"), StatusReserva.ACEITA);
        ReservaResponse response = reservaService.atualizarReserva(req, 1L);
        assertEquals(StatusReserva.ACEITA, response.statusReserva());
        assertEquals(Time.valueOf("15:00:00"), response.horaDaReserva());
        verify(ReservaDao).save(any(Reserva.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarReservaInexistente() {
        when(ReservaDao.findById(2L)).thenReturn(Optional.empty());
        ReservaRequest req = new ReservaRequest(1L, 1L, new Date(), Time.valueOf("15:00:00"), StatusReserva.ACEITA);
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> reservaService.atualizarReserva(req, 2L));
        assertEquals("ID de reserva buscado não encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveDeletarReserva() {
        cliente.getReservas().add(reserva);
        estacionamento.getReservas().add(reserva);

        when(ReservaDao.findById(1L)).thenReturn(Optional.of(reserva));
        when(ReservaDao.save(any(Reserva.class))).thenReturn(reserva);

        reservaService.deletarReserva(1L);

        assertFalse(reserva.getStatus());
        assertFalse(cliente.getReservas().contains(reserva));
        assertFalse(estacionamento.getReservas().contains(reserva));
        verify(ReservaDao).save(reserva);
    }

    @Test
    void deveLancarExcecaoAoDeletarReservaInexistente() {
        when(ReservaDao.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> reservaService.deletarReserva(2L));
        assertEquals("ID de reserva buscado não encontrado no sistema!", ex.getMessage());
    }
}
