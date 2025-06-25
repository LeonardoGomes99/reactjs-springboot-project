package application.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import application.authentication.dto.UserDTO;
import application.authentication.entities.User;
import application.authentication.repositories.RoleRepository;
import application.authentication.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    public List<UserDTO> listar(){
        return repository.findAll().stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO inserir(UserDTO dto){

        return new UserDTO();
    }

    public UserDTO editar(){
        return new UserDTO();
    }

    public void excluir(){
    }

    private UserDTO toUserDTO(User user) {
        return new UserDTO(user);
    }
}
