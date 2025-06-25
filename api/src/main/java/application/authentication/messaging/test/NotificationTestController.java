package application.authentication.messaging.test;

import application.authentication.dto.NotificationDTO;
import application.authentication.messaging.publisher.NotificationPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/notificacoes")
public class NotificationTestController {

    private final NotificationPublisher publisher;

    public NotificationTestController(NotificationPublisher publisher) {
        this.publisher = publisher;
    }

    // Função para criar a Notificacao
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<String> enviar(@RequestBody NotificationDTO dto) {
        publisher.sendNotificationWithDTO(dto);
        return ResponseEntity.ok("Notificação enviada para " + dto.getUsername());
    }
}
