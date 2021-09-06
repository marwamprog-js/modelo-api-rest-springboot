package com.maltadev.cursoapi.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Usuario implements UserDetails {

	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String nome;
	
	@Column(unique = true)
	private String login;
		
	private String senha;
	
	@CPF(message = "cpf inválido")
	private String cpf;
	
	private String token = "";
	
	private String cep;
	private String logradouro;
	private String bairro;
	private String localidade;
	private String uf;
	
	@OneToMany(mappedBy = "usuario", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Telefone> telefones = new ArrayList<Telefone>();
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(
				name = "usuarios_role", 
				uniqueConstraints = @UniqueConstraint(
				columnNames = {"usuario_id", "role_id"}, 
				name = "unique_role_user"
			), 
			joinColumns = @JoinColumn(
				name = "usuario_id", 
				referencedColumnName = "id", 
				table = "usuario", 
				unique = false,
				foreignKey = @ForeignKey(name = "usuario_fk", value = ConstraintMode.CONSTRAINT)
			), 
			inverseJoinColumns = @JoinColumn(
				name="role_id", 
				referencedColumnName = "id", 
				table = "role",
				unique = false,
				updatable = false,
				foreignKey = @ForeignKey(name = "role_fk", value = ConstraintMode.CONSTRAINT)
			)
	)
	private List<Role> roles = new ArrayList<Role>();
		
	
	@JsonFormat(pattern = "dd/MM/yyyy") //Retorno do json para o front
	@Temporal(TemporalType.DATE) //tipo de dado a ser gravado
	@DateTimeFormat(iso = ISO.DATE, pattern = "dd/MM/yyyy")
	private Date dataNascimento;
	
	@ManyToOne
	private Profissao profissao;
	
	private BigDecimal salario;
	
	
	
	public BigDecimal getSalario() {
		return salario;
	}
	public void setSalario(BigDecimal salario) {
		this.salario = salario;
	}
	public void setProfissao(Profissao profissao) {
		this.profissao = profissao;
	}
	public Profissao getProfissao() {
		return profissao;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getCpf() {
		return cpf;
	}	
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getLocalidade() {
		return localidade;
	}
	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}	
	public void setToken(String token) {
		this.token = token;
	}
	public String getToken() {
		return token;
	}
	public List<Telefone> getTelefones() {
		return telefones;
	}
	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(id, other.id);
	}
	
	
	/*
	 * São os acessos do usuário
	 * ROLE_ADMIN
	 * */
	@Override
	public Collection<Role> getAuthorities() {
		return roles;
	}
	
	@JsonIgnore
	@Override
	public String getPassword() {
		return this.senha;
	}
	
	@JsonIgnore
	@Override
	public String getUsername() {
		return this.login;
	}
	
	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return true;
	}
	
	
}
