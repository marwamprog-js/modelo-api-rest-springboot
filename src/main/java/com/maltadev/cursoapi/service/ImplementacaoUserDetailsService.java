package com.maltadev.cursoapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.maltadev.cursoapi.model.Usuario;
import com.maltadev.cursoapi.repository.UsuarioRepository;


@Service
public class ImplementacaoUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		/*
		 * Consultar no banco o usuário
		 * */
		Usuario usuario = usuarioRepository.findUserByLogin(username);
		
		if(usuario == null) {
			throw new UsernameNotFoundException("Usuário não foi encontrado");
		}
		
		
		//User retorna toda a parte da autorização.
		return new User(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
	}


	/*
	 * Insert da Permissão padrão para o usuário
	 * */
	public void insereAcessoPadrao(Long id) {
		
		/*
		 * 1º passo - Retorna a chave que restringe duplicidade no banco ao cadastrar 
		 * a mesma permissão para usuários direfentes
		 * */
		String constraint = usuarioRepository.consultaConstraintRole();
		
		/*
		 * 2º passo - Remove a chave que restringe duplicidade no banco
		 * */
		if(constraint != null) {
			jdbcTemplate.execute(" alter table usuarios_role drop constraint " + constraint);
		}
		
		/*
		 * 3º passo - insere acesso padrão
		 * */
		usuarioRepository.insereAcessoRolePadrão(id);
		
		
		
	}

}
