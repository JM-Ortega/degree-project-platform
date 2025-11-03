package co.edu.unicauca.frontend.services;

import co.edu.unicauca.frontend.infra.dto.UsuarioDTO;
import org.springframework.web.client.RestTemplate;

public class DocenteService{
    private final RestTemplate restTemplate;
    private final String baseUrlDocente = "http://localhost:8080/api/academic/docentes";

    public DocenteService() {
        this.restTemplate = new RestTemplate();
    }

    public UsuarioDTO obtenerDocenteActivo(String correo) {
        return restTemplate.getForObject(baseUrlDocente + "/" + correo, UsuarioDTO.class);
    }

    public int countProyectosEnTramiteDocente(String correo) {
        return restTemplate.getForObject(baseUrlDocente + "/countProyectos/" + correo, Integer.class);
    }

    public boolean docenteTieneCupo(String correo) {
        return countProyectosEnTramiteDocente(correo) < 7;
    }

    public int maxVersionAnteproyecto(long id) {
        return 1;
    }

}
