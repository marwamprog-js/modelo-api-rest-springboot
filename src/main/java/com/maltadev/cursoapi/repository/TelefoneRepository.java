package com.maltadev.cursoapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.maltadev.cursoapi.model.Telefone;

@Repository
public interface TelefoneRepository extends CrudRepository<Telefone, Long>{

}
