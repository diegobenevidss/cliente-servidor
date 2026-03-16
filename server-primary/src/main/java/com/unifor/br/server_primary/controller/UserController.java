package com.unifor.br.server_primary.controller;

import com.unifor.br.server_primary.model.User;
import com.unifor.br.server_primary.service.UserService; // Importando o SEU service
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService; // Usando o seu nome

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody User user) {
        try {
            userService.salvarEReplicar(user); // Chamando o método
            return ResponseEntity.ok("Dado salvo no Primário e replicado!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno no servidor líder.");
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}