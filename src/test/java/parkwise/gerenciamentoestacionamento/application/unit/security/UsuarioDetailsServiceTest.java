package parkwise.gerenciamentoestacionamento.application.unit.security;

import parkwise.gerenciamentoestacionamento.dao.UsuarioDao;
import parkwise.gerenciamentoestacionamento.infraestructure.security.UsuarioDetailService;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.Usuario;
import parkwise.gerenciamentoestacionamento.model.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioDao repository;

    @InjectMocks
    private UsuarioDetailService service;

    @Test
    void deveRetornarUserDetailsQuandoUsuarioEncontrado() {
        Usuario user = DonoEstacionamento.builder()
                .email("rafael@senai.com")
                .senha("123")
                .role(Role.ADMIN)
                .build();

        when(repository.findByEmail("rafael@senai.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("rafael@senai.com");

        assertEquals("rafael@senai.com", details.getUsername());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(repository.findByEmail("x@senai.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("x@senai.com"));
    }
}
