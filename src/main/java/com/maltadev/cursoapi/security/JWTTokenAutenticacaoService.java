package com.maltadev.cursoapi.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.maltadev.cursoapi.ApplicationContextLoad;
import com.maltadev.cursoapi.model.Usuario;
import com.maltadev.cursoapi.repository.UsuarioRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component //Fazer injeção de dependencia
public class JWTTokenAutenticacaoService {

	//Tempo de validade do token
	private static final long EXPIRATION_TIME = 172800000;

	//Uma senha unica para compor a autenticação e ajudar na segurança
	private static final String SECRET = "SenhaExtremamenteSecreta";

	//Prefixo padrão de Token
	private static final String TOKEN_PREFIX = "Bearer";

	private static final String HEADER_STRING = "Authorization";


	/*
	 * Gerando token de autenticação e adicionando 
	 * ao cabecalho e resposta HTTP
	 * */
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {

		//Mostragem de token
		String JWT = Jwts.builder() //Chama o gerador de token
				.setSubject(username) //Adiciona o usuário
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //Tempo de expiração
				.signWith(SignatureAlgorithm.HS256, SECRET).compact(); //Compactação e algoritimo de geração de senha

		//Junta o token com o prefix
		String token = TOKEN_PREFIX + " " + JWT; //Bearer 894h6gf4hdgh6s4h68g4h6

		//Adiciona no cabecalho http
		response.addHeader(HEADER_STRING, token); //Authorization: Bearer 894h6gf4hdgh6s4h68g4h6

		ApplicationContextLoad.getApplicationContext()
		.getBean(UsuarioRepository.class).atualizaTokenUser(JWT, username);
		
		//Liberando respostas para portas direferentes que usam api ou no caso clientes web
		liberacaoCors(response);

		//Escreve token como resposta no corpo do Http
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");

	}



	/*
	 * Retorna o usuário validado com o token ou caso
	 * não seja válido retorna null
	 * */
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

		//Pega o token enviado no cabecalho HTTP
		String token = request.getHeader(HEADER_STRING);


		try {
			if(token != null) {


				String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim(); //trim retira espaços

				//Faz a validação do token do usuário na requisição
				String user = Jwts.parser().setSigningKey(SECRET) //Bearer 894h6gf4hdgh6s4h68g4h6
						.parseClaimsJws(tokenLimpo) //894h6gf4hdgh6s4h68g4h6
						.getBody().getSubject(); //João Silva - Retorna somente o usuário

				if(user != null) {

					Usuario usuario = ApplicationContextLoad.getApplicationContext()
							.getBean(UsuarioRepository.class).findUserByLogin(user);

					if(usuario != null) {

						if(tokenLimpo.equalsIgnoreCase(usuario.getToken())) {
							return new UsernamePasswordAuthenticationToken(
									usuario.getLogin(), 
									usuario.getSenha(), 
									usuario.getAuthorities()
									);
						}
					}
				} 			
			}
			
			liberacaoCors(response);
			
		}catch (ExpiredJwtException e) {
			try {
				response.getOutputStream().println("Seu TOKEN está expirado, faça o login ou informe um novo token para autenticação");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}		

		liberacaoCors(response);
		
		return null; //Não autorizado

	}


	/*
	 * Liberação Cors
	 * */
	private void liberacaoCors(HttpServletResponse response) {

		if(response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}

		if(response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}

		if(response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}

		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}

	}
}
