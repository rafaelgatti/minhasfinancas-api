package com.app.minhasfinancas.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.app.minhasfinancas.exception.ErroAutenticacao;
import com.app.minhasfinancas.exception.RegraNegocioException;
import com.app.minhasfinancas.model.entity.Usuario;
import com.app.minhasfinancas.model.repository.UsuarioRepository;
import com.app.minhasfinancas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test
	public void deveSalvarUsuario() {
		//cenario
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = new Usuario();
		usuario.setId(1l);
		usuario.setNome("usuario");
		usuario.setEmail("usuario@email.com");
		usuario.setSenha("senha");
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		//verificacao
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("usuario");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("usuario@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	}
	
	@Test
	public void naoDeveSalvarUsuario() {
		//cenario
		String email = "usuario@email.com";
		Usuario usuario = new Usuario();
		usuario.setEmail(email);
		
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		//acao
		org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () -> service.salvarUsuario(usuario));
		
		//verificacao
		Mockito.verify(repository, Mockito.never()).save(usuario);
	}
	
	@Test
	public void deveAutenticarUsuarioComSucesso() {
		//cenario
		String email = "usuario@email.com";
		String senha = "senha";
		
		Usuario usuario = new Usuario();
		usuario.setEmail(email);
		usuario.setSenha(senha);
		
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//acao
		Usuario result = service.autenticar(email, senha);
		
		//verificacao
		Assertions.assertThat(result).isNotNull();
		
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarusuarioComEmailInformado() {
		//cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//acao
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("usuario@email.com", "senha") );
	
		//verificacao
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado!");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		
		//cenario
		String senha = "senha";
		Usuario usuario = new Usuario();
		usuario.setEmail("usuario@email.com");
		usuario.setSenha(senha);
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//acao
		Throwable exception =  Assertions.catchThrowable(() -> service.autenticar("usuario@email.com", "123"));
		
		//verificacao
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida!");
	}
	
	@Test
	public void deveValidarEmail() {
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//acao
		service.validarEmail("email@email.com");
		
		//verificacao
	}
	
	@Test
	public void deveLancarErroAoValidarEmailCadastrado() {
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//acao
		//Throwable exception = Assertions.catchThrowable( () -> service.validarEmail("usuario@email.com") );
		org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () -> service.validarEmail("usuario@email.com"));
		
		//verificacao
		//Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).isNotNull();
	}
}
