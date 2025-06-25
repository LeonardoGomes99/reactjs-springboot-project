package application.authentication.dto;

import java.time.LocalDate;
import java.util.UUID;

public class UserDetailsDTO {

    private UUID id;

    private UUID userId;

    private String nome;

    private LocalDate dataNascimento;

    private Integer sexo;

    private String endereco;

    private String telefone;

    public UserDetailsDTO() {
    }

    public UserDetailsDTO(UUID id, UUID userId, String nome, LocalDate dataNascimento, Integer sexo, String endereco, String telefone) {
        this.id = id;
        this.userId = userId;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.endereco = endereco;
        this.telefone = telefone;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Integer getSexo() {
        return sexo;
    }

    public void setSexo(Integer sexo) {
        this.sexo = sexo;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
