package com.maltadev.cursoapi.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.maltadev.cursoapi.model.Telefone;
import com.maltadev.cursoapi.model.UserChart;
import com.maltadev.cursoapi.model.UserReport;
import com.maltadev.cursoapi.model.Usuario;
import com.maltadev.cursoapi.model.UsuarioDTO;
import com.maltadev.cursoapi.repository.TelefoneRepository;
import com.maltadev.cursoapi.repository.UsuarioRepository;
import com.maltadev.cursoapi.service.ImplementacaoUserDetailsService;
import com.maltadev.cursoapi.service.ServiceRelatorio;

/* 
 * Libera acesso para todas as api. 
 * Qualquer lugar vai acessar
	@CrossOrigin 
	@CrossOrigin(origins = "*")
 */


/*
 * Libera acesso apenas para um dominio
	@CrossOrigin(origins = "https://www.jdevtreinamentos.com")
	@CrossOrigin(origins = {"https://www.jdevtreinamentos.com", "https://www.jdevtreinamentos.com"})
 */


@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	
	@Autowired
	private ServiceRelatorio serviceRelatorio;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;


	/*
	 * Listar
	 * 
	 * Supondo que o carregamento do usuário seja um processo lento
	 * e queremos controlar ele com CACHE para agilizar o processo
	 * */
	//@CrossOrigin(origins = "https://www.jdevtreinamentos.com") Lista apenas requisições deste dominio
	@GetMapping(value = "/", produces = "application/json")
	//@Cacheable("cacheListaUsuarios") //Apenas vai gerando cache e não exclui com atualização
	@CacheEvict(value = "cacheListaUsuarios", allEntries = true) //Identifica o cache e remove o cache quando haver atualização
	@CachePut("cacheListaUsuarios") //Identifica que tem atualização e coloca no cache
	public ResponseEntity<Page<Usuario>> listar() throws InterruptedException {

		//Pesquisa apenas os cinco registros da primeira página ordenado por nome
		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));
		
		Page<Usuario> usuarios = usuarioRepository.findAll(page);

		/*
		 * Simulando demora de requisição		 * 
		 * 
		Thread.sleep(6000);//Segura o código por 6 segundos
		 */

		return new ResponseEntity<Page<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	
	
	/*
	 * Carregar Paginação
	 * */
	@GetMapping(value = "/page/{pagina}", produces = "application/json")
	@CachePut("cacheusuariopage")
	public ResponseEntity<Page<Usuario>> listarPagina(@PathVariable("pagina") int pagina) throws InterruptedException {

		//Pesquisa apenas os cinco registros da primeira página ordenado por nome
		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
		
		Page<Usuario> usuarios = usuarioRepository.findAll(page);

		return new ResponseEntity<Page<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	

	/*
	 * Buscar por ID
	 * 
	 * Classe DTO serve para ocultar o nome dos atributos em tempo de execução
	 * */
	@GetMapping(value = "V1/{idUsuario}", produces = "application/json")
	public ResponseEntity<UsuarioDTO> listarPorIdV1(@PathVariable(value = "idUsuario") Long idUsuario){

		Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);


		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}	


	/*
	 * Buscar por ID 
	 * 
	 * */
	@GetMapping(value = "/{idUsuario}", produces = "application/json")
	@CachePut("cacheBuscaId")
	public ResponseEntity<Usuario> listarPorId(@PathVariable(value = "idUsuario") Long idUsuario){

		Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);


		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}	


	/*
	 * Buscar por NOME
	 *  
	 * */
	@GetMapping(value = "usuarioPorNome/{nome}", produces = "application/json")
	@CachePut("cachebuscanome")
	public ResponseEntity<Page<Usuario>> listarPorNome(@PathVariable(value = "nome") String nome){

		PageRequest pageRequest = null;
		Page<Usuario> usuarios = null;
		
		if(nome == null || (nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) { //Não informou o nome para busca
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			usuarios = usuarioRepository.findAll(pageRequest);
		} else { // Informou o nome
			
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));			
			usuarios = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}
		
		

		return new ResponseEntity<Page<Usuario>>(usuarios, HttpStatus.OK);
	}	
	
	
	/*
	 * Buscar por NOME por Página 1,2,3...
	 * */
	@GetMapping(value = "usuarioPorNome/{nome}/page/{page}", produces = "application/json")
	@CachePut("cachebuscanome")
	public ResponseEntity<Page<Usuario>> listarPorNomePage(@PathVariable(value = "nome") String nome, @PathVariable(value = "page") int page){

		PageRequest pageRequest = null;
		Page<Usuario> usuarios = null;
		
		if(nome == null || (nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) { //Não informou o nome para busca
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			usuarios = usuarioRepository.findAll(pageRequest);
		} else { // Informou o nome
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			
			usuarios = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}
		
		

		return new ResponseEntity<Page<Usuario>>(usuarios, HttpStatus.OK);
	}	



	/*
	 *###########====> URI 
	 *
	 * Listar por ID V1
	 * 
	@GetMapping(value = "V1/{idUsuario}", produces = "application/json")
	public ResponseEntity<Usuario> listarPorIdV1(@PathVariable(value = "idUsuario") Long idUsuario){

		Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);


		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}

	/*
	 * #####===> HEADER 
	 *
	 * Listar por ID V2
	 * 
	@GetMapping(value = "/{idUsuario}", produces = "application/json", headers = "X-API-Version=v2")
	public ResponseEntity<Usuario> listarPorIdV2(@PathVariable(value = "idUsuario") Long idUsuario){

		Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);


		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	 */



	/*
	 * Salvar
	 * */
	//@CrossOrigin(origins = "https://www.jdevtreinamentos.com") Salvar apenas requisições deste dominio
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception{


		/*
		 * Método vai amarrar os telefones ao usuário
		 * */
		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}


		if(usuario.getCep() != null) {
			//Consumindo uma api publica externa ViaCEP
			URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			String cep = "";
			StringBuilder jsonCep = new StringBuilder();

			while((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}
			
			
			Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);

			usuario.setCep(userAux.getCep());
			usuario.setLogradouro(userAux.getLogradouro());
			usuario.setBairro(userAux.getBairro());
			usuario.setLocalidade(userAux.getLocalidade());
			usuario.setUf(userAux.getUf());

		}

		


		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);		
		Usuario usuarioCadastrado = usuarioRepository.save(usuario);
		
		/*
		 * Método responsavel por cadastrar permissão padrão no banco de dados.
		 * */
		implementacaoUserDetailsService.insereAcessoPadrao(usuarioCadastrado.getId());
		

		return new ResponseEntity<Usuario>(usuarioCadastrado, HttpStatus.OK);

	}


	/*
	 * Atualizar
	 * */
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario){

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}

		Usuario userTemporario = usuarioRepository.findById(usuario.getId()).get();

		if (!userTemporario.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}

		Usuario usuarioAtualizado = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarioAtualizado, HttpStatus.OK);

	}


	/*
	 * DELETE USUARIO
	 * */
	@DeleteMapping(value = "/{idUsuario}", produces = "aplication/json")
	public String delete(@PathVariable(value = "idUsuario") Long idUsuario) {

		usuarioRepository.deleteById(idUsuario);

		return "OK";
	}
	
	
	/*
	 * REMOVE TELEFONE
	 * */
	@DeleteMapping(value = "/removerTelefone/{id}", produces = "application/text")
	public String removeTelefone(@PathVariable("id") Long id) {
		
		telefoneRepository.deleteById(id);
		
		return "OK";
		
	}
	
	
	/*
	 * RELATORIO PDF
	 * */
	@GetMapping(value = "/relatorio", produces = "application/text")
	public ResponseEntity<String> downloadRelatorio(HttpServletRequest request) throws Exception {
		
		byte[] pdf = serviceRelatorio.gerarRelatorio("relatorio-usuario", new HashMap(),
				request.getServletContext());
		
		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
		
		return new ResponseEntity<String>(base64Pdf, HttpStatus.OK);
		
	}
	
	/*
	 * RELATORIO PDF
	 * */
	@PostMapping(value = "/relatorio/", produces = "application/text")
	public ResponseEntity<String> downloadRelatorioParam(HttpServletRequest request, @RequestBody UserReport userReport) throws Exception {
		
		//Formato tipo Date para conversao de string
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat dateFormatParam = new SimpleDateFormat("yyyy-MM-dd");
		
		String dataInicio = dateFormatParam.format(dateFormat.parse(userReport.getDataInicio()));
		String dataFim = dateFormatParam.format(dateFormat.parse(userReport.getDataFim()));
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("DATA_INICIO", dataInicio);
		params.put("DATA_FIMom	", dataFim);
		
		byte[] pdf = serviceRelatorio.gerarRelatorio("relatorio-usuario-param", params,
				request.getServletContext());
		
		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
		
		return new ResponseEntity<String>(base64Pdf, HttpStatus.OK);
		
	}
	
	
	/*
	 * Mátodo para GRAFICO
	 * */
	@GetMapping(value = "/grafico", produces = "application/json")
	public ResponseEntity<UserChart> grafico(){
		
		UserChart userChart = new UserChart();
		
		String sql = "select array_agg(nome) from usuario where salario > 0 and nome <> ''"
				+ " union all "
				+ "select cast(array_agg(salario) as character varying[]) from usuario where salario > 0 and nome <> ''";
		
		List<String> resultdado = jdbcTemplate.queryForList(sql, String.class);
		
		if(!resultdado.isEmpty()) {
			String nomes = resultdado.get(0).replaceAll("\\{", "").replaceAll("\\}", "");
			String salarios = resultdado.get(1).replaceAll("\\{", "").replaceAll("\\}", "");
			
			userChart.setNome(nomes);
			userChart.setSalario(salarios);
		}
		
		return new ResponseEntity<UserChart>(userChart, HttpStatus.OK);
	}
	
	
}
