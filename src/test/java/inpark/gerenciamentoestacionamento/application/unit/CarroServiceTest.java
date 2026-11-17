package inpark.gerenciamentoestacionamento.application.unit;

import inpark.gerenciamentoestacionamento.application.dto.requests.CarroRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.CarroResponse;
import inpark.gerenciamentoestacionamento.application.services.CarroService;
import inpark.gerenciamentoestacionamento.dao.CarroDao;
import inpark.gerenciamentoestacionamento.dao.ClienteDao;
import inpark.gerenciamentoestacionamento.model.entities.Carro;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
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
public class CarroServiceTest {

    @Mock
    private CarroDao CarroDao;
    @Mock
    private ClienteDao ClienteDao;
    @InjectMocks
    private CarroService carroService;

    private Cliente cliente;
    private Carro carro;
    private CarroRequest carroRequest;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setCarros(new ArrayList<>());

        carro = new Carro();
        carro.setId(1L);
        carro.setPlaca("EUD-8679");
        carro.setModelo("Corsa");
        carro.setCor("Fuxia");
        carro.setStatus(true);
        carro.setCliente(cliente);

        carroRequest = new CarroRequest(
                1L,
                "EUD-8679",
                "Corsa",
                "Fuxia"
        );
    }

    @Test
    void deveListarCarros() {
        when(CarroDao.findByStatusTrue()).thenReturn(List.of(carro));
        List<CarroResponse> lista = carroService.listarCarros();
        assertEquals(1, lista.size());
        assertEquals("EUD-8679", lista.get(0).placa());
        verify(CarroDao).findByStatusTrue();
    }

    @Test
    void deveBuscarCarroPorId() {
        when(CarroDao.findById(1L)).thenReturn(Optional.of(carro));
        CarroResponse response = carroService.buscarPorId(1L);
        assertEquals("EUD-8679", response.placa());
        verify(CarroDao).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarIdNaoCadastrado() {
        when(CarroDao.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> carroService.buscarPorId(2L));
        assertEquals("ID buscado não foi encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveCadastrarCarro() {
        when(ClienteDao.findById(1L)).thenReturn(Optional.of(cliente));
        when(CarroDao.save(any(Carro.class))).thenReturn(carro);

        CarroResponse response = carroService.cadastrarCarro(carroRequest);
        assertEquals("EUD-8679", response.placa());
        assertTrue(response.status());
        verify(CarroDao).save(any(Carro.class));
    }

    @Test
    void deveLancarExcecaoAoCadastrarComClienteInexistente() {
        when(ClienteDao.findById(2L)).thenReturn(Optional.empty());
        CarroRequest req = new CarroRequest(2L, "EUD-8679", "Corsa", "Fuxia");
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> carroService.cadastrarCarro(req));
        assertEquals("Cliente não encontrado com no sistema.", ex.getMessage());
    }

    @Test
    void deveAtualizarCarro() {
        Cliente novoCliente = new Cliente();
        novoCliente.setId(2L);
        novoCliente.setCarros(new ArrayList<>());

        CarroRequest req = new CarroRequest(2L, "ABC-1234", "Onix", "Preto");

        when(CarroDao.findById(1L)).thenReturn(Optional.of(carro));
        when(ClienteDao.findById(2L)).thenReturn(Optional.of(novoCliente));
        when(CarroDao.save(any(Carro.class))).thenReturn(carro);

        CarroResponse response = carroService.atualizarCarro(req, 1L);
        assertEquals("ABC-1234", response.placa());
        assertEquals("Onix", response.modelo());
        assertEquals("Preto", response.cor());
        verify(CarroDao).save(any(Carro.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarCarroOuClienteInexistente() {
        when(CarroDao.findById(2L)).thenReturn(Optional.empty());
        when(ClienteDao.findById(1L)).thenReturn(Optional.of(cliente));
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> carroService.atualizarCarro(carroRequest, 2L));
        assertEquals("Carro ou Cliente não encontrado no sistema.", ex.getMessage());
    }

    @Test
    void deveDeletarCarro() {
        cliente.getCarros().add(carro);
        when(CarroDao.findById(1L)).thenReturn(Optional.of(carro));
        when(CarroDao.save(any(Carro.class))).thenReturn(carro);

        carroService.deletarCarro(1L);

        assertFalse(carro.getStatus());
        assertFalse(cliente.getCarros().contains(carro));
        verify(CarroDao).save(carro);
    }

    @Test
    void deveLancarExcecaoAoDeletarCarroInexistente() {
        when(CarroDao.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> carroService.deletarCarro(2L));
        assertEquals("Carro não encontrado no sistema.", ex.getMessage());
    }
}
