package parkwise.gerenciamentoestacionamento.application.unit.usuarios;

import parkwise.gerenciamentoestacionamento.application.dto.requests.usuario.DonoRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.usuario.DonoResponse;
import parkwise.gerenciamentoestacionamento.application.mappers.usuario.DonoMapper;
import parkwise.gerenciamentoestacionamento.application.services.usuario.DonoService;
import parkwise.gerenciamentoestacionamento.dao.DonoDao;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import parkwise.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DonoServiceTest {

    @Mock
    private DonoDao repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private DonoService service;

    @Test
    void deveCadastrarDonoValido() {
        DonoRequest dto = new DonoRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        DonoEstacionamento entidade = DonoMapper.toEntity(dto);

        when(repository.save(any())).thenReturn(entidade);

        DonoResponse salvo = service.cadastrarDono(dto);

        assertNotNull(salvo);
        assertEquals("Pedro", salvo.nome());
        verify(repository).save(any());
    }

    @Test
    void deveBuscarDonoPorId() {
        DonoRequest dto = new DonoRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        DonoEstacionamento entidade = DonoMapper.toEntity(dto);
        when(repository.findById(1L)).thenReturn(Optional.of(entidade));
        DonoResponse resultado = service.buscarPorId(1L);
        assertEquals("Pedro", resultado.nome());
        verify(repository).findById(1L);
    }

    @Test
    void deveLancarIdDesconhecidoExceptionAoBuscarIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class,
                () -> service.buscarPorId(99L));
        assertEquals("ID buscado não foi encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveAtualizarDonoComSucesso() {
        DonoRequest existente = new DonoRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        DonoRequest dtoAtualizado = new DonoRequest(
                "Arthur",
                "arthur@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        DonoEstacionamento salvo = DonoMapper.toEntity(dtoAtualizado);
        salvo.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(DonoMapper.toEntity(existente)));
        when(repository.save(any())).thenReturn(salvo);

        DonoResponse resultado = service.atualizarDono(dtoAtualizado, 1L);

        assertEquals("Arthur", resultado.nome());
        assertEquals("arthur@gmail.com", resultado.email());
        verify(repository).save(any());
    }

    @Test
    void deveDeletarDonoExistente() {
        DonoEstacionamento dono = new DonoEstacionamento();
        dono.setId(1L);
        dono.setStatus(true);

        when(repository.findById(1L)).thenReturn(Optional.of(dono));

        service.deletarDono(1L);

        assertFalse(dono.getStatus(), "espera-se que o status seja marcado como false (soft-delete)");
        verify(repository).findById(1L);
        verify(repository).save(dono);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void deveLancarIdDesconhecidoExceptionAoDeletarDonoInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class,
                () -> service.deletarDono(99L));

        assertEquals("Dono buscado não cadastrado no sistema", ex.getMessage());
    }
}
