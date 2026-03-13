package com.unifor.br.server_primary.controller;

import com.unifor.br.server_primary.model.User;
import com.unifor.br.server_primary.service.ReplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final ReplicationService replicationService;

    public UserController(ReplicationService replicationService) {
        this.replicationService = replicationService;
    }

    // Recebe do Gateway
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody User user) {
        try {
            replicationService.salvarEReplicar(user);
            return ResponseEntity.ok("Dado salvo no Primário e replicado!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno no servidor líder.");
        }
    }

    // Usado pelo Gateway para saber se este servidor está vivo
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}