package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.infra.FormatoADTO;
import co.edu.unicauca.coordinatorservice.service.IntegrationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FormatoAListener {
    private final IntegrationService integrationService;

    public FormatoAListener(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @RabbitListener(queues = "${messaging.queues.coordinator}")
    public void onFormatoAEvent(FormatoADTO formatoADTO) {
        System.out.println("FormatoA recibido desde RabbitMQ: " + formatoADTO.getNombre());
        integrationService.handleFormatoAUpdate(formatoADTO);
    }
}
