package application.authentication.messaging.consumer;

import application.authentication.config.rabbitmq.RabbitMQConfig;
import application.authentication.dto.NotificationDTO;
import application.authentication.entities.Notification;
import application.authentication.repositories.NotificationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationConsumer {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receberMensagem(String mensagem) {
        System.out.println("Notificacao: " + mensagem);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumir(NotificationDTO dto) {
        String username = dto.getUsername();
        String mensagem = dto.getMensagem();

        // Persistir no banco como "não lida"
        Notification entidade = new Notification(UUID.randomUUID(), username, mensagem, false);
        notificationRepository.save(entidade);

        // Enviar via WebSocket para o canal pessoal do usuário
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notificacoes",
                entidade
        );
    }
}
