package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.infra.ProyectoDTO;
import co.edu.unicauca.coordinatorservice.service.IntegrationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProjectListener {
    private final IntegrationService integrationService;

    public ProjectListener(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @RabbitListener(queues = "${messaging.queues.coordinator}")
    public void onProjectEvent(ProyectoDTO proyectoDTO) {
        System.out.println("Proyecto recibido desde RabbitMQ: " + proyectoDTO.getTitulo());
        integrationService.handleProjectUpdate(proyectoDTO);
    }
}
