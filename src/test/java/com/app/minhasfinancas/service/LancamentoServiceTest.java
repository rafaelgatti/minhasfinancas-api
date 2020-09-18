package com.app.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.app.minhasfinancas.exception.RegraNegocioException;
import com.app.minhasfinancas.model.entity.Lancamento;
import com.app.minhasfinancas.model.entity.Usuario;
import com.app.minhasfinancas.model.enums.StatusLancamento;
import com.app.minhasfinancas.model.enums.TipoLancamento;
import com.app.minhasfinancas.model.repository.LancamentoRepository;
import com.app.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.app.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarLancamento() {
		//cenario
		Lancamento lancamentoFuturo = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoFuturo);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoFuturo)).thenReturn(lancamentoSalvo);
		
		//acao
		Lancamento lancamento = service.salvar(lancamentoFuturo);
		
		//verificacao
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
		
	}
	
	@Test
	public void naoDeveSalvarLancamento() {
		//cenario
		Lancamento lancamentoFuturo = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoFuturo);
		
		//acao e verificacao
		Assertions.catchThrowableOfType( () -> service.salvar(lancamentoFuturo), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoFuturo);
		
	}
	
	@Test
	public void deveAtualizarLancamento() {
		//cenario
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//acao
		service.salvar(lancamentoSalvo);
		
		//verificacao
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoAtualizarLancamento() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//acao e verificacao
		Assertions.catchThrowableOfType( () -> service.atualizar(lancamento), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamento);
		
	}
	
	@Test
	public void deveRemoverLancamento() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		
		//acao
		service.deletar(lancamento);
		
		//verificacao]
		Mockito.verify(repository).delete(lancamento);
		
	}
	
	@Test
	public void deveLancarErroAoRemoverLancamento() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//acao
		Assertions.catchThrowableOfType( () -> service.deletar(lancamento), NullPointerException.class);
		
		//verificacao]
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		//acao
		List<Lancamento> resultado = service.buscar(lancamento);
		
		//verificacao
		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}
	
	@Test
	public void deveAtualizarStatusLancamento() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		//acao
		service.atualizarStatus(lancamento, novoStatus);
		
		//verificacao
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}
	
	@Test
	public void deveObterLancamentoPorId() {
		//cenario
		Long id = 1L;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//acao
		Optional<Lancamento> resutado = service.obterPorId(id);
		
		//verificacao
		Assertions.assertThat(resutado.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioQuandoLancamentoNaoExiste() {
		//cenario
		Long id = 1L;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		//acao
		Optional<Lancamento> resutado = service.obterPorId(id);
		
		//verificacao
		Assertions.assertThat(resutado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErrosAoValidarLancamento() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida!");
		
		lancamento.setDescricao("");
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida!");
		
		lancamento.setDescricao("Salário");
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido!");
		
		lancamento.setMes(0);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido!");
		
		lancamento.setMes(13);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido!");
		
		lancamento.setMes(1);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido!");
		
		lancamento.setAno(202);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido!");
		
		lancamento.setAno(2020);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário!");
		
		lancamento.setUsuario(new Usuario());
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário!");
		
		lancamento.getUsuario().setId(1L);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido!");
		
		lancamento.setValor(BigDecimal.ZERO);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido!");
		
		lancamento.setValor(BigDecimal.TEN);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento!");
		
		lancamento.setTipo(TipoLancamento.RECEITA);
	}
	
	
}
