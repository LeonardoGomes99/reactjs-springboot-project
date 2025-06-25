package application.authentication.controllers;

import application.authentication.entities.Notification;
import application.authentication.entities.UserDetails;
import application.authentication.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notificacoes")
public class NotificationController {

    @Autowired
    private NotificationRepository repo;


    @PostMapping("/{id}/lida")
    public ResponseEntity<?> marcarLida(@PathVariable UUID id) {
        Notification n = repo.findById(id).orElseThrow();
        n.setLida(true);
        repo.save(n);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<Notification> listar(@AuthenticationPrincipal UserDetails user) {
        return repo.findByUsername(user.getNome());
    }
}
