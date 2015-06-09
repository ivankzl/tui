package ar.edu.um.service;

import java.math.BigDecimal;
import java.util.List;

import ar.edu.um.model.*;

public interface IPersonaService {

	void persistPersona(Persona persona);
	List<Persona> list();
	Persona findPersonaByClave(Integer clave);
	Persona findPersonaByPerID(BigDecimal Per_ID);
	Persona validarLogin(BigDecimal login_id, String password);

}
