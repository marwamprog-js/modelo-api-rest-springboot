package com.maltadev.cursoapi.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maltadev.cursoapi.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	
	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "update usuario set token = ?1 where login = ?2")
	void atualizaTokenUser(String token, String login);
	
	@Query("select u from Usuario u where u.nome like %?1%")
	List<Usuario> findUserByNome(String nome);
	
	
	@Transactional
	@Modifying
	@Query(value = "update usuario set senha = ?1 where id = ?2", nativeQuery = true)
	void updateSenha(String senha, Long codUser);
	
	
	/*
	 * 1º passo - Retorna a chave que restringe duplicidade no banco ao cadastrar 
	 * a mesma permissão para usuários direfentes
	 * */
	@Query(value = "select constraint_name from information_schema.constraint_column_usage where table_name = 'usuarios_role' and column_name = 'role_id'\n"
			+ "and constraint_name <> 'unique_role_user'", nativeQuery = true)
	String consultaConstraintRole();
	
	
	
	
	/*
	 * 3º passo - insere acesso padrão
	 * */
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "insert into usuarios_role (usuario_id, role_id)\n"
			+ "values (?1, (select id from role where nome_role = 'ROLE_USER'))")
	void insereAcessoRolePadrão(Long idUser);

	
	
	/*
	 * BUSCA POR NOME Paginação
	 * */
	default Page<Usuario> findUserByNamePage(String nome, PageRequest pageRequest) {
		
		Usuario usuario = new Usuario();
		usuario.setNome(nome);
		
		//Configurando para pesquisar por NOME e PAGINAÇÃO
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers
						.contains().ignoreCase());
		
		Example<Usuario> exemple = Example.of(usuario, exampleMatcher);
		
		Page<Usuario> retorno = findAll(exemple, pageRequest);
		
		return retorno;
		
	}
	
}
