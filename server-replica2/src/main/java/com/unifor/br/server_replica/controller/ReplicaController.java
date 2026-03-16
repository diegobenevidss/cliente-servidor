package com.unifor.br.server_replica.controller;

import com.unifor.br.server_replica.model.User;
import com.unifor.br.server_replica.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReplicaController {

    private final UserRepository userRepository;

    public ReplicaController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // A campainha exata que o Primário está tocando!
    @PostMapping("/replicate")
    public ResponseEntity<String> receberReplicacao(@RequestBody User user) {
        // 👇 A MÁGICA AQUI: Apagamos o ID do Primário para a Réplica gerar o dela!
        user.setId(null);

        userRepository.save(user); // Agora o JPA sabe que é um INSERT novo
        System.out.println("✅ Dado sincronizado com sucesso: " + user.getName());
        return ResponseEntity.ok("Replicado com sucesso");
    }

    // Uma rota extra para vermos os dados pelo navegador depois
    @GetMapping("/all-data")
    public ResponseEntity<List<User>> getAllData() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}