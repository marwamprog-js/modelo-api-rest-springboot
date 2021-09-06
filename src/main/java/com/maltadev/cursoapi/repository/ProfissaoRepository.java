package com.maltadev.cursoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maltadev.cursoapi.model.Profissao;

@Repository
public interface ProfissaoRepository extends JpaRepository<Profissao, Long> {

}
