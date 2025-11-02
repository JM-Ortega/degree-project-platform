package co.edu.unicauca.frontend.services.auth;

import co.edu.unicauca.frontend.dto.LoginRequestDto;
import co.edu.unicauca.frontend.dto.LoginResponseDto;
import co.edu.unicauca.frontend.dto.RegistroPersonaDto;

/**
 * Contrato que define las operaciones HTTP disponibles en el microservicio de autenticación (Auth).
 *
 * <p>
 * Esta interfaz abstrae la comunicación entre el frontend y el backend, permitiendo cambiar
 * la implementación (por ejemplo, de {@link co.edu.unicauca.frontend.infra.http.HttpAuthApi}
 * a otra basada en cliente REST) sin afectar las capas superiores.
 * </p>
 */
public interface AuthApi {

    /**
     * Inicia sesión en el sistema con las credenciales proporcionadas.
     *
     * @param request objeto con los datos de inicio de sesión (correo, contraseña y rol).
     * @return una respuesta con la información de sesión y token devueltos por el backend.
     * @throws Exception si ocurre un error de conexión o si el backend devuelve un estado no exitoso.
     */
    LoginResponseDto login(LoginRequestDto request) throws Exception;

    /**
     * Registra una nueva persona en el sistema.
     *
     * @param request DTO con la información del usuario a registrar.
     * @throws Exception si ocurre un error de red o si el backend devuelve una respuesta no exitosa.
     */
    void register(RegistroPersonaDto request) throws Exception;
}
