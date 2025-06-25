package application.authentication.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "tb_notifications")
public class Notification {
    @Id
    private UUID id;
    private String username;
    private String mensagem;
    private boolean lida;

    public Notification() {}
    public Notification(UUID id, String username, String mensagem, boolean lida) {
        this.id = id;
        this.username = username;
        this.mensagem = mensagem;
        this.lida = lida;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }
}
