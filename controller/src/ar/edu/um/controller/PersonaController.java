package ar.edu.um.controller;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.model.Aluleg;
import ar.edu.um.model.Content;
import ar.edu.um.model.ContentUserInfo;
import ar.edu.um.model.Credencial;
import ar.edu.um.model.Domicilio;
import ar.edu.um.model.Facultad;
import ar.edu.um.model.Persona;
import ar.edu.um.model.RespuestaJSON;
import ar.edu.um.model.RespuestaJSONLogin;
import ar.edu.um.model.RespuestaJSONUserInfo;
import ar.edu.um.service.IAlulegService;
import ar.edu.um.service.ICredencialService;
import ar.edu.um.service.IDomicilioService;
import ar.edu.um.service.IFacultadService;
import ar.edu.um.service.IPersonaService;

@RestController
public class PersonaController {

	
	static final String AB = "0123456789abcdefghijklmnopkrstuvwxyz";
	static Random rnd = new Random();

	String randomString( int len ) 
	{
	   StringBuilder sb = new StringBuilder( len );
	   for( int i = 0; i < len; i++ ) 
	      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
	   return sb.toString();
	}
	
	/** 
	 * Url: http://<base_url>/welcome/user_id/
	   Devuelve: Json con los siguientes atributos:
       ▪ status: <status code>
       ▪ content
	 *Este método recibe como parámetro el número de credencial de la persona, que se encuentra en la 
	 *tarjeta de débito. Mientras esté en localhost, se puede probar con la siguiente credencial:
	 *http://localhost:8080/welcome/4517660104006712
	 **/
	@RequestMapping(value = "/welcome/{cre_numero}", method = RequestMethod.GET)
	public RespuestaJSON findCredencial(@PathVariable("cre_numero") BigDecimal cre_numero) {
		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IPersonaService perService = (IPersonaService) context.getBean("personaService");
		ICredencialService creService = (ICredencialService) context.getBean("credencialService");
	
		try{
			Credencial credencial = creService.findCredencialByCreNumero(cre_numero);
			System.out.println("cre_per_id de credencial = " + credencial.getCre_per_id());
			Persona persona = perService.findPersonaByPerID(credencial.getCre_per_id());
		
			//IDomicilioService domService = (IDomicilioService) context.getBean("domicilioService");
			//Domicilio domicilio = domService.findDomicilioByPerID(persona.getPer_ID()); esto usarlo cuando necesite el mail
		
			Content content = new Content();
			content.setLogin_id(persona.getPer_ID());
			content.setName(persona.getPer_Nombre() + " " + persona.getPer_Apellido());
			content.setPhoto("http://um.edu.ar/cursos/resources/images/marca.png");
			String[] array = {"r1"};
			content.setRoles(array);
		
			RespuestaJSON respuesta = new RespuestaJSON();
			respuesta.setContent(content);
			respuesta.setStatus(200);
	
			return respuesta;
		}catch(NullPointerException e){

			RespuestaJSON respuesta = new RespuestaJSON();
			respuesta.setStatus(-2);
			return respuesta;
		}

	}
	
	/** 
	 * Parámetros:
	 * ▪ login_id (GET) Identificador del usuario para el acceso a servicios privados.
	 * ▪password (GET) Contraseña de la cuenta de usuario.
	 * Url: https://<base_url>/login/login_id/password
	 * Devuelve: Json con los siguientes atributos:
	 * ▪ status: <status code>
	 * ▪ session_token: Token de sesión para el acceso a los métodos privados
	 * 
	 **/
	@RequestMapping(value = "/login/{login_id}/{password}", method = RequestMethod.GET)
	public RespuestaJSONLogin login(@PathVariable("login_id") BigDecimal login_id, @PathVariable("password") String password){
		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IPersonaService perService = (IPersonaService) context.getBean("personaService");
		
		Persona persona = perService.validarLogin(login_id, password);
		System.out.println(persona);
		RespuestaJSONLogin respuesta = new RespuestaJSONLogin();
		
		if (persona == null){
			respuesta.setStatus(-1);
		}else{

			respuesta.setStatus(200);
			String rand = randomString(30);
			
			respuesta.setSession_token(rand);
		}
		
		
		return respuesta;
	}
	
	
	
	@RequestMapping(value = "/get_user_info/{cre_numero}", method = RequestMethod.GET)
	public RespuestaJSONUserInfo getUserInfo(@PathVariable("cre_numero") BigDecimal cre_numero){
		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IPersonaService perService = (IPersonaService) context.getBean("personaService");
		ICredencialService creService = (ICredencialService) context.getBean("credencialService");
		try{
			Credencial credencial = creService.findCredencialByCreNumero(cre_numero);
			System.out.println("cre_per_id de credencial = " + credencial.getCre_per_id());
			//DE ACA SACO EL USER_ID, NOMBRE, APELLIDO
			Persona persona = perService.findPersonaByPerID(credencial.getCre_per_id());
		
			//EMAIL, TELEFONO, sacarlo del domicilio
			IDomicilioService domService = (IDomicilioService) context.getBean("domicilioService");
			Domicilio domicilio = domService.findDomicilioByPerID(persona.getPer_ID());
		
		
			IAlulegService aluService = (IAlulegService) context.getBean("alulegService");
			Aluleg aluleg = aluService.findFacultadByPerID(persona.getPer_ID());
		
		
		
			IFacultadService facuService = (IFacultadService) context.getBean("facultadService");
			Facultad facultad = facuService.findFacultadByID(aluleg.getALe_Car_ID());
	
		
			String[] arrayAdm = {facultad.getFac_Nombre()};
			String[] arrayRel = {"alumno"};
			String[] arrayRole = {"r1"};
		
			ContentUserInfo content = new ContentUserInfo();
			
			content.setUser_id(persona.getPer_ID());
			content.setEmail(domicilio.getDom_e_mail());
			content.setTlf(domicilio.getDom_Telefono());
			content.setName(persona.getPer_Nombre());
			content.setSurname(persona.getPer_Apellido());
			content.setPhoto("http://um.edu.ar/cursos/resources/images/marca.png");
			content.setAdmin_unit(arrayAdm);
			//RELATION ->ojo, no tenemos si es alumno o administrativo o prof...
			content.setRelation(arrayRel);
			//ROLES ->ojo, no tenemos roles en la bd
			content.setRoles(arrayRole);
			//STATUS ->ojo, no tenemos status en la bd
			content.setStatus("activo");
		
			RespuestaJSONUserInfo respuesta = new RespuestaJSONUserInfo();
			respuesta.setContent(content);
			respuesta.setStatus(200);
		
		
			return respuesta;
		}catch (NullPointerException e){
			System.out.println("nullPointerException cachada");
			RespuestaJSONUserInfo respuesta = new RespuestaJSONUserInfo();
			respuesta.setStatus(-2);
			return respuesta;
		}
		
	}
 
}
