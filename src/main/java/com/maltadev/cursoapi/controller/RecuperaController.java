package com.maltadev.cursoapi.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.maltadev.cursoapi.ObjetoErro;
import com.maltadev.cursoapi.model.Usuario;
import com.maltadev.cursoapi.repository.UsuarioRepository;
import com.maltadev.cursoapi.service.ServiceEnviaEmail;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperaController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ServiceEnviaEmail serviceEnviaEmail;
	
	
	@ResponseBody
	@PostMapping(value = "/")
	public ResponseEntity<ObjetoErro> recuperar(@RequestBody Usuario login) throws Exception {
		
		ObjetoErro objetoErro = new ObjetoErro();
		
		Usuario user = usuarioRepository.findUserByLogin(login.getLogin());
		
		if(user == null) {
			objetoErro.setCode("404"); //Não encontrado
			objetoErro.setError("Usuário não encontrado");
		} else {
			/***
			 * Rotina de envio de e-mail
			 */
			
			
			//Gerando nova senha para usuário
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String senhaNova = dateFormat.format(Calendar.getInstance().getTime());
			
			String senhaCriptografada = new BCryptPasswordEncoder().encode(senhaNova);
			
			
			//Pega nova senha gerado e salva no banco para usuário
			usuarioRepository.updateSenha(senhaCriptografada, user.getId());
			
			//Enviando o Email
			serviceEnviaEmail.enviarEmail("Recuperação de senha", user.getLogin(), 
					"Sua nova senha é: " + senhaNova);
			
			
			objetoErro.setCode("200"); //encontrado
			objetoErro.setError("Acesso enviado para seu e-mail");
		}
		
		return new ResponseEntity<ObjetoErro>(objetoErro, HttpStatus.OK);
	}
	
}
