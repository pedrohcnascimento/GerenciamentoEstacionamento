package inpark.gerenciamentoestacionamento.application.unit;

import inpark.gerenciamentoestacionamento.application.dto.requests.EstacionamentoRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.EstacionamentoResponse;
import inpark.gerenciamentoestacionamento.application.services.EstacionamentoService;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.EstacionamentoRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.usuario.DonoRepository;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import inpark.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
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
public class EstacionamentoServiceTest {

    @Mock
    private EstacionamentoRepository estacionamentoRepository;
    @Mock
    private DonoRepository donoRepository;
    @InjectMocks
    private EstacionamentoService estacionamentoService;

    private Estacionamento estacionamento;
    private EstacionamentoRequest estacionamentoRequest;
    private DonoEstacionamento dono;

    @BeforeEach
    void setUp() {
        estacionamento = new Estacionamento();
        estacionamento.setId(1L);
        estacionamento.setNome("EstacioPlay");
        estacionamento.setEndereco("Rua das Flores");
        estacionamento.setCEP("12345-678");
        estacionamento.setNumero("123");
        estacionamento.setFotoUrl("foto.jpg");
        estacionamento.setNumeroAlvaraDeFuncionamento("123456789");
        estacionamento.setHoraFechamento(LocalTime.of(22, 0));
        estacionamento.setHoraAbertura(LocalTime.of(8, 0));
        estacionamento.setVagasPreferenciais(10);
        estacionamento.setMaxVagas(100);
        estacionamento.setNumeroDeEscrituraImovel("987654321");
        estacionamento.setStatus(true);

        estacionamento.setAvaliacoes(new ArrayList<>());
        estacionamento.setAcessos(new ArrayList<>());
        estacionamento.setReservas(new ArrayList<>());
        estacionamento.setGerentes(new ArrayList<>());

        estacionamentoRequest = new EstacionamentoRequest(
                "EstacioPlay",
                "Rua das Flores",
                "12345-678",
                "123",
                "foto.jpg",
                "123456789",
                LocalTime.of(22, 0),
                LocalTime.of(8, 0),
                10,
                100,
                "987654321"
        );

        dono = new DonoEstacionamento();
        dono.setId(1L);
        dono.setEstacionamentos(new ArrayList<>());
        dono.getEstacionamentos().add(estacionamento);
        estacionamento.setDono(dono);
    }

    @Test
    void deveListarTodosOsEstacionamentos() {
        when(estacionamentoRepository.findByStatusTrue()).thenReturn(List.of(estacionamento));
        List<EstacionamentoResponse> lista = estacionamentoService.listarTodosOsEstacionamentos();
        assertEquals(1, lista.size());
        assertEquals("EstacioPlay", lista.get(0).nome());
        verify(estacionamentoRepository).findByStatusTrue();
    }

    @Test
    void deveBuscarEstacionamentoPorId() {
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        EstacionamentoResponse response = estacionamentoService.buscarPorId(1L);
        assertEquals("EstacioPlay", response.nome());
        verify(estacionamentoRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarIdNaoCadastrado() {
        when(estacionamentoRepository.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> estacionamentoService.buscarPorId(2L));
        assertEquals("ID buscado não foi encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveCadastrarEstacionamento() {
        when(donoRepository.findById(1L)).thenReturn(Optional.of(dono));
        when(estacionamentoRepository.save(any(Estacionamento.class))).thenReturn(estacionamento);

        EstacionamentoResponse response = estacionamentoService.cadastrarEstacionamento(estacionamentoRequest, 1L);
        assertEquals("EstacioPlay", response.nome());
        assertTrue(response.status());
        verify(estacionamentoRepository).save(any(Estacionamento.class));
    }

    @Test
    void deveLancarExcecaoAoCadastrarComDonoInexistente() {
        when(donoRepository.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> estacionamentoService.cadastrarEstacionamento(estacionamentoRequest, 2L));
        assertEquals("O Id do dono fornecido não foi encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveAtualizarEstacionamento() {
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(estacionamentoRepository.save(any(Estacionamento.class))).thenReturn(estacionamento);

        EstacionamentoResponse response = estacionamentoService.atualizarEstacionamento(estacionamentoRequest, 1L);
        assertEquals("EstacioPlay", response.nome());
        verify(estacionamentoRepository).save(any(Estacionamento.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarIdNaoCadastrado() {
        when(estacionamentoRepository.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> estacionamentoService.atualizarEstacionamento(estacionamentoRequest, 2L));
        assertEquals("O Id do estacionamento fornecido não foi encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveDesativarEstacionamento() {
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(estacionamentoRepository.save(any(Estacionamento.class))).thenReturn(estacionamento);

        estacionamentoService.desativarEstacionamento(1L);

        verify(estacionamentoRepository).save(estacionamento);
        assertFalse(estacionamento.getStatus());
    }

    @Test
    void deveLancarExcecaoAoDesativarIdNaoCadastrado() {
        when(estacionamentoRepository.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> estacionamentoService.desativarEstacionamento(2L));
        assertEquals("O Id do estacionamento fornecido não foi encontrado no sistema!", ex.getMessage());
    }
}
