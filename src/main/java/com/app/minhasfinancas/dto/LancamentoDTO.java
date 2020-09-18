package com.app.minhasfinancas.dto;

import java.math.BigDecimal;

public class LancamentoDTO {

	private Long id;
	private String descricao;
	private Integer mes;
	private Integer ano;
	private BigDecimal valor;
	private Long usuario;
	private String tipo;
	private String status;
	
	public Long getId() {
		return id;
	}
	public String getDescricao() {
		return descricao;
	}
	public Integer getMes() {
		return mes;
	}
	public Integer getAno() {
		return ano;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public Long getUsuario() {
		return usuario;
	}
	public String getTipo() {
		return tipo;
	}
	public String getStatus() {
		return status;
	}
	
}
