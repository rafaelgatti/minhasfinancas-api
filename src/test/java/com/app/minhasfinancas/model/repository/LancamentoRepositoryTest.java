package com.app.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.app.minhasfinancas.model.entity.Lancamento;
import com.app.minhasfinancas.model.enums.StatusLancamento;
import com.app.minhasfinancas.model.enums.TipoLancamento;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LancamentoRepositoryTest {
	
	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	
	@Test
	public void deveSalvarLancamento() {
		//cenario
		Lancamento lancamento = criarLancamento();
		
		//acao
		repository.save(lancamento);
		
		//verificacao
		assertThat(lancamento.getId()).isNotNull();
	}
	
	@Test
	public void deveRemoverLancamento() {
		//cenario
		Lancamento lancamento = criarEPersistirLancamento();
		
		//acao
		repository.delete(lancamento);
		
		//verificacao
		Lancamento lancamentoExcluido = entityManager.find(Lancamento.class, lancamento.getId());
		assertThat(lancamentoExcluido).isNull();
	}
	
	@Test
	public void deveAtualizarLancamento() {
		//cenario
		Lancamento lancamento = criarEPersistirLancamento();
		
		//acao
		lancamento.setDescricao("CONTA");
		lancamento.setAno(2019);
		lancamento.setStatus(StatusLancamento.CANCELADO);
		repository.save(lancamento);
		
		//verificacao
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("CONTA");
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2019);
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
	}
	
	@Test
	public void deveBuscarLancamentoPorId() {
		//cenario
		Lancamento lancamento = criarEPersistirLancamento();
		
		//acao
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		//verificacao
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}
	
	
	public static Lancamento criarLancamento() {
		Lancamento lancamento = new Lancamento();
		lancamento.setDescricao("lancamento");
		lancamento.setAno(2020);
		lancamento.setMes(9);
		lancamento.setValor(BigDecimal.valueOf(10));
		lancamento.setTipo(TipoLancamento.RECEITA);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		lancamento.setDataCadastro(LocalDate.now());
		
		return lancamento;
	}
	
	private Lancamento criarEPersistirLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		
		return lancamento;
	}

}
