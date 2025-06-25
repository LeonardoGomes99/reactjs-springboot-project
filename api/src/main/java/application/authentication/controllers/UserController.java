package application.authentication.controllers;

import application.authentication.dto.UserDTO;
import application.authentication.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;


    @GetMapping
    public ResponseEntity<List<UserDTO>> listar(){
        return ResponseEntity.status(HttpStatus.OK).body(service.listar());
    }

    @PostMapping
    public ResponseEntity<UserDTO> criar(@Valid @RequestBody UserDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.inserir(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> editar(@PathVariable("id") UUID id, @Valid @RequestBody UserDTO dto){
        return ResponseEntity.status(HttpStatus.OK).body(new UserDTO());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> excluir(@PathVariable("id") UUID id){
        return ResponseEntity.noContent().build();
    }
}
