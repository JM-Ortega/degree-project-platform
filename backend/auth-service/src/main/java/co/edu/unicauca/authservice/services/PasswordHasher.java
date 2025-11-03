package co.edu.unicauca.authservice.services;

/**
 * Servicio para hashear y verificar contraseñas.
 * Se define como interfaz para poder cambiar el algoritmo
 * sin afectar al resto del microservicio.
 */
public interface PasswordHasher {

    /**
     * Genera el hash seguro de la contraseña.
     *
     * @param rawPassword contraseña en texto plano
     * @return hash generado
     */
    String hash(char[] rawPassword);

    /**
     * Verifica si la contraseña en texto plano coincide con el hash almacenado.
     *
     * @param plainPassword contraseña en texto plano
     * @param hashedPassword hash previamente almacenado
     * @return true si coincide, false en otro caso
     */
    boolean verify(char[] plainPassword, String hashedPassword);
}
