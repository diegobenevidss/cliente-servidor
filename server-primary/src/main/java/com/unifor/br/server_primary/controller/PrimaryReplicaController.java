package com.unifor.br.server_primary.controller;

import com.unifor.br.server_primary.model.User;
import com.unifor.br.server_primary.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrimaryReplicaController {

    private final UserRepository userRepository;

    public PrimaryReplicaController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Essa é a campainha da submissão!
    @PostMapping("/replicate")
    public ResponseEntity<String> receberReplicacao(@RequestBody User user) {
        user.setId(null);
        userRepository.save(user);
        System.out.println("✅ [Modo Réplica] Dado recebido da nova líder e sincronizado: " + user.getName());
        return ResponseEntity.ok("Replicado com sucesso");
    }
}