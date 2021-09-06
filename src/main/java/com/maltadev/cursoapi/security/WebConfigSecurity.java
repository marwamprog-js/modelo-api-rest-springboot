package com.maltadev.cursoapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.maltadev.cursoapi.service.ImplementacaoUserDetailsService;

/*
 * Mapeia URLs, Endereços, Autoriza ou bloqueia acessos a URLs
 * */
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	
	/*
	 * Configura as solicitações de acesso HTTP
	 * */
	@Override
		protected void configure(HttpSecurity http) throws Exception {
			
		
			http.cors();
		
			//Ativando a proteção contra usuários que não estão validados por token
			http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
									
			//Ativando a permissão para acesso a página inicial do sistema  EX: sistema.com.br/*/
			.disable().authorizeRequests().antMatchers("/").permitAll()
			.antMatchers("/index", "/recuperar/**").permitAll()
			.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
			
			
			//URL de logout - Redireciona após o user deslogar do sistema
			.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
			
			
			//Mapeia URL de logout e invalida o usuário
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))			
				
			
			
			/*
			 * Filtra requisições de login para autenticação
			 * */
			.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
			
			
			/*
			 * Filtra demais requisições para verificar a presenção do token JWT no HEADER HTTP
			 * */
			.addFilterBefore(new JWTApiAuthenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
		}
	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		//Service que irá consultar o usuário no banco de dados
		auth.userDetailsService(implementacaoUserDetailsService)

		//Padrão de codificação de senha
		.passwordEncoder(new BCryptPasswordEncoder());
	}

}
