package co.edu.unicauca.frontend.infra.operation;

import co.edu.unicauca.frontend.dto.RegistroPersonaDto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Clase utilitaria encargada de validar los datos ingresados en el formulario
 * de registro antes de enviarlos al backend.
 *
 * <p>
 * Permite detectar errores comunes (campos vacíos, formato de correo inválido,
 * contraseñas débiles, roles no permitidos, etc.) y devolverlos en un
 * {@link Map} estructurado, donde la clave corresponde al nombre del campo
 * y el valor es el mensaje de error.
 * </p>
 *
 * <p>
 * Esta versión del validador está pensada para el frontend,
 * por lo que trabaja con cadenas de texto en lugar de enums.
 * </p>
 */
public final class RegistrationValidator {

    private RegistrationValidator() {
    }

    /** Patrón que valida correos institucionales del dominio unicauca.edu.co */
    private static final Pattern EMAIL_UNICAUCA =
            Pattern.compile("^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$", Pattern.CASE_INSENSITIVE);

    /** Patrón de contraseña: mínimo 6 caracteres, con al menos un dígito, una mayúscula y un carácter especial permitido */
    private static final Pattern PASSWORD =
            Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!¿?*._-]).{6,}$");

    /** Patrón de número de celular colombiano de 10 dígitos */
    private static final Pattern CEL10 = Pattern.compile("^\\d{10}$");

    /**
     * Realiza la validación de los datos de registro a partir de un
     * {@link RegistroPersonaDto}.
     *
     * @param dto objeto con los datos ingresados por el usuario en el formulario de registro.
     * @return mapa de errores: clave = nombreCampo, valor = mensaje de error correspondiente.
     */
    public static Map<String, String> validate(RegistroPersonaDto dto) {
        return validate(
                dto.getNombres(),
                dto.getApellidos(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getPrograma(),
                dto.getRoles(),
                dto.getCelular(),
                dto.getDepartamento()
        );
    }

    /**
     * Valida de manera detallada los campos individuales del formulario de registro.
     *
     * @param nombres      nombres de la persona.
     * @param apellidos    apellidos de la persona.
     * @param email        correo electrónico a registrar.
     * @param password     contraseña en texto plano.
     * @param programa     nombre del programa académico (texto, no enum).
     * @param roles        lista de nombres de roles seleccionados.
     * @param celular      número de celular opcional.
     * @param departamento departamento elegido (solo aplica para Docente o JefeDeDepartamento).
     * @return mapa de errores: clave = nombreCampo, valor = mensaje de error correspondiente.
     */
    public static Map<String, String> validate(
            String nombres,
            String apellidos,
            String email,
            String password,
            String programa,
            List<String> roles,
            String celular,
            String departamento
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        // Nombres y apellidos
        if (isBlank(nombres)) {
            errors.put("nombres", "El nombre es obligatorio");
        }
        if (isBlank(apellidos)) {
            errors.put("apellidos", "El apellido es obligatorio");
        }

        // Correo institucional
        if (isBlank(email)) {
            errors.put("email", "El correo electrónico es obligatorio");
        } else if (!EMAIL_UNICAUCA.matcher(email.trim()).matches()) {
            errors.put("email", "El correo debe pertenecer al dominio @unicauca.edu.co");
        }

        // Contraseña
        if (isBlank(password)) {
            errors.put("password", "La contraseña es obligatoria");
        } else if (!PASSWORD.matcher(password).matches()) {
            errors.put("password", "La contraseña no cumple con los requisitos de seguridad");
        }

        // Celular (opcional)
        if (!isBlank(celular) && !CEL10.matcher(celular.trim()).matches()) {
            errors.put("celular", "El número de celular debe tener exactamente 10 dígitos");
        }

        // Programa académico
        if (isBlank(programa)) {
            errors.put("programa", "El programa académico es obligatorio");
        }

        // Roles
        if (roles == null || roles.isEmpty()) {
            errors.put("roles", "Debe seleccionar al menos un rol");
        } else {
            boolean contieneRolNoPermitido = roles.stream()
                    .anyMatch(r -> !r.equalsIgnoreCase("Estudiante") && !r.equalsIgnoreCase("Docente"));
            if (contieneRolNoPermitido) {
                errors.put("roles", "No puede registrarse con ese rol. Use Estudiante o Docente.");
            }

            boolean requiereDepartamento = roles.stream()
                    .anyMatch(r -> r.equalsIgnoreCase("Docente") || r.equalsIgnoreCase("JefeDeDepartamento"));

            if (requiereDepartamento && isBlank(departamento)) {
                errors.put("departamento", "El departamento es obligatorio para docentes y jefes de departamento");
            }
        }

        return errors;
    }

    /**
     * Determina si una cadena es nula o está vacía después de eliminar espacios en blanco.
     *
     * @param s cadena a evaluar.
     * @return {@code true} si la cadena es nula o vacía, {@code false} en caso contrario.
     */
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
