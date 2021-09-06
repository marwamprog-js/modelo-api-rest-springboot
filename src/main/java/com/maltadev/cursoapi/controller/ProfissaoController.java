package com.maltadev.cursoapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maltadev.cursoapi.model.Profissao;
import com.maltadev.cursoapi.repository.ProfissaoRepository;

@RestController
@RequestMapping("/profissao")
public class ProfissaoController {

	@Autowired
	private ProfissaoRepository profissaoRepository;

	
	/**
	 * Método para LISTAR profissoes.
	 * */
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<Profissao>> profissoes() {

		List<Profissao> profissoes = profissaoRepository.findAll();

		return new ResponseEntity<List<Profissao>>(profissoes, HttpStatus.OK);

	}

}
