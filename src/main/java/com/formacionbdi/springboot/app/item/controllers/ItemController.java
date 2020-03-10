package com.formacionbdi.springboot.app.item.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formacionbdi.springboot.app.item.models.Item;
import com.formacionbdi.springboot.app.item.models.Producto;
import com.formacionbdi.springboot.app.item.models.service.ItemService;
import com.netflix.discovery.shared.Application;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RefreshScope
@RestController
public class ItemController {

	private static Logger log = LoggerFactory.getLogger(ItemController.class);

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("serviceFeign")
	private ItemService itemService;

	@Value("${configuracion.texto}")
	private String texto;

	@Autowired
	private RestTemplate rest;

	@GetMapping(value = "/jwt", produces = "application/json")
	public String getJwt() {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
 		headers.setBasicAuth("frontendapp", "12345");
 		headers.add("username", "admin");
 		
 		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
 		map.add("username", "admin");
 		map.add("password", "12345");
 		map.add("grant_type", "password");
		HttpEntity<?> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<String> response = rest.exchange("http://SERVICIO-ZUUL-SERVER/api/security/oauth/token",
				HttpMethod.POST, request, String.class);
  
		return response.getBody();
	}
	
	//consultando al servicio de oauth2 para obtener el token y luego consultar al servicio 
	//de listar por id del microservicio productos pasandole el token para poder realizar la consulta
	@PostMapping(value = "/jwtp", produces = "application/json")
	public ResponseEntity<String> getJwtPost(@RequestBody Map<String, Object> body) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
 		headers.setBasicAuth("frontendapp", "12345");
 		headers.add("username", "admin");
 		 
 		
 		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
 		map.add("username", body.get("usuario").toString());
 		map.add("password",body.get("password").toString());
 		map.add("grant_type", body.get("grant_type").toString());
		HttpEntity<?> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<Map> response = rest.exchange("http://SERVICIO-ZUUL-SERVER/api/security/oauth/token",
				HttpMethod.POST, request, Map.class);
		String token = "Bearer "+response.getBody().get("access_token");		
		
		
		//OTRO SERVICIO USANDO EL TOKEN GENERADO
		HttpHeaders headersItems = new HttpHeaders();
		headersItems.add("Authorization", token);
		
		HttpEntity<?> requestItems = new HttpEntity<>(headersItems);
		
		
		ResponseEntity<String> responseItems = rest.exchange("http://SERVICIO-ZUUL-SERVER/api/productos/ver/2",
				HttpMethod.GET, requestItems, String.class);
		
  
		return responseItems;
	}

	@GetMapping("/listar")
	public List<Item> listar() {
		return itemService.findAll();
	}

	@GetMapping("/probandoTimeout")
	public String probandoTimeout() {
		return itemService.timeout();
	}

//	@HystrixCommand(fallbackMethod = "metodoAlternativo")
	@GetMapping("/ver/{id}/cantidad/{cantidad}")
	public Item detalle(@PathVariable Long id, @PathVariable Integer cantidad) throws Exception {
		return itemService.findById(id, cantidad);
	}

	// para manejar tolerancia a fallos con histrix, hay que agregar la anotacion
	// @HistrixCommand
	// y en el string fallBackMethod pasarle el nombre del metodo alternativo en
	// caso de error.
	// el metodo alternativo tiene que tener mismos parametros que el original
	@HystrixCommand(fallbackMethod = "metodoAlternativoError")
	@GetMapping("/servicioError")
	public String detalleError() throws Exception {
		throw new Exception("Error con el servicio");
	}

	public String metodoAlternativoError() {
		return "tenemos problemas con el servicio, intentelo mas tarde";

	}

	public Item metodoAlternativo(Long id, Integer cantidad) {
		Item item = new Item();
		Producto producto = new Producto();

		item.setCantidad(cantidad);
		producto.setId(id);
		producto.setNombre("Camara Sony");
		producto.setPrecio(500.00);
		item.setProducto(producto);
		return item;

	}

	@GetMapping("/obtener-config")
	public ResponseEntity<?> obtenerConfig(@Value("${server.port}") String puerto) {

		log.info(texto);

		Map<String, String> json = new HashMap<>();
		json.put("texto", texto);
		json.put("puerto", puerto);

		if (env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev")) {
			json.put("autor.nombre", env.getProperty("configuracion.autor.nombre"));
			json.put("autor.email", env.getProperty("configuracion.autor.email"));
		}

		return new ResponseEntity<Map<String, String>>(json, HttpStatus.OK);
	}

	@PostMapping("/crear")
	@ResponseStatus(HttpStatus.CREATED)
	public Producto crear(@RequestBody Producto producto) {
		return itemService.save(producto);
	}

	@PutMapping("/editar/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Producto editar(@RequestBody Producto producto, @PathVariable Long id) {
		return itemService.update(producto, id);
	}

	@DeleteMapping("/eliminar/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable Long id) {
		itemService.delete(id);
	}
}
