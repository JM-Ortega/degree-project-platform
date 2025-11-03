package co.edu.unicauca.academicprojectservice.infra.config;

import co.edu.unicauca.academicprojectservice.Service.DocenteService;
import co.edu.unicauca.academicprojectservice.infra.dto.UserDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocenteConsumerService {

    @Autowired
    private DocenteService docenteService;

    @RabbitListener(queues = "${messaging.queues.project}")
    public void ingresoUsuario(UserDto user) {
        docenteService.procesarDocente(user);
    }
}
