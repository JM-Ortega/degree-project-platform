package co.edu.unicauca.coordinatorservice.service;

import co.edu.unicauca.coordinatorservice.infra.DTOS.FormatoADTO;
import co.edu.unicauca.coordinatorservice.infra.DTOS.EstadoFormatoA;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Service
public class FormatoAService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${messaging.exchange.main}")
    private String mainExchange;

    @Value("${messaging.routing.formatAApprovedByCoordinator}")
    private String routingKeyFormatAApproved;

    private final FormatoARepository formatoARepository;

    public FormatoAService(FormatoARepository formatoARepository, RabbitTemplate rabbitTemplate) {
        this.formatoARepository = formatoARepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public FormatoA actualizarFormato(Long id, MultipartFile archivo, String nuevoEstado,  String nombreArchivo, String horaActual) throws IOException {
        FormatoA formato = formatoARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formato A no encontrado con id: " + id));

        if (formato.getNroVersion() > 3) {
            throw new IllegalStateException("No se pueden subir más de 3 versiones del Formato A para este proyecto.");
        }

        formato.setBlob(archivo.getBytes());
        formato.setNombreFormatoA(nombreArchivo);

        // Convertir la cadena a LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate fecha = LocalDate.parse(horaActual, formatter);
        formato.setFechaSubida(fecha);

        if(nuevoEstado.toLowerCase().equals("rechazado")){
            formato.setEstadoFormatoA(EstadoFormatoA.OBSERVADO);
        }else if(nuevoEstado.toLowerCase().equals("aceptado")){
            formato.setEstadoFormatoA(EstadoFormatoA.APROBADO);
        }

        formatoARepository.save(formato);

        FormatoADTO dto = new FormatoADTO(
                formato.getId(),
                formato.getProyectoId(),
                formato.getNroVersion(),
                formato.getNombreFormatoA(),
                formato.getFechaSubida(),
                formato.getBlob(),
                formato.getEstadoFormatoA());

        System.out.println("Enviando mensaje a exchange: " + mainExchange + " con routingKey: " + routingKeyFormatAApproved);


        rabbitTemplate.convertAndSend(mainExchange, routingKeyFormatAApproved, dto);

        return formato;
    }
}
