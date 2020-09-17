package com.app.minhasfinancas.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.minhasfinancas.exception.ErroAutenticacao;
import com.app.minhasfinancas.exception.RegraNegocioException;
import com.app.minhasfinancas.model.entity.Usuario;
import com.app.minhasfinancas.model.repository.UsuarioRepository;
import com.app.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	private UsuarioRepository repository;
	
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não encontrado!");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida!");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existeUsuario = repository.existsByEmail(email);
		
		
		if(existeUsuario) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com esse email!");
		}
	}

	
}
