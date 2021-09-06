package com.maltadev.cursoapi.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maltadev.cursoapi.model.Usuario;


/*
 * Estabelece nosso gerenciador de token
 * */
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter{

	
	//Configurando o gerenciador de autenticação
	protected JWTLoginFilter(String url, AuthenticationManager authenticationManager) {
		
		//Obriga a autenticar a URL
		super(new AntPathRequestMatcher(url));
		
		//Gerenciador de autenticação
		setAuthenticationManager(authenticationManager);
		
		
		
	}

	
	/*
	 * Retorno usuário ao processar autenticação
	 * */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		//Pegando o token para validar		
		Usuario user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
		
		//Retornar o usuario login, senha e acesso
		return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getLogin(), user.getSenha()));
	}
	
	
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		new JWTTokenAutenticacaoService().addAuthentication(response, authResult.getName());
		
	}

}
