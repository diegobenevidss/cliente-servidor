package com.sistema.distribuido.replica.controller;

import com.sistema.distribuido.replica.model.User;
import com.sistema.distribuido.replica.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReplicaController {

    private final UserRepository userRepository;

    public ReplicaController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Recebe exclusivamente do Servidor Primário
    @PostMapping("/replicate")
    public ResponseEntity<String> receberReplicação(@RequestBody User user) {
        userRepository.save(user); // Salva no banco H2 desta réplica
        System.out.println("Dado sincronizado: " + user.getNome());
        return ResponseEntity.ok("Replicado com sucesso");
    }

    // Usado pelo Gateway
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    // Rota crucial para o TESTE 4: Devolve todos os dados do banco
    @GetMapping("/all-data")
    public ResponseEntity<List<User>> getAllData() {
        List<User> todosOsUsuarios = userRepository.findAll();
        return ResponseEntity.ok(todosOsUsuarios);
    }
}