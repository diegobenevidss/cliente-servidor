package com.unifor.br.server_primary.service;

import com.unifor.br.server_primary.model.User;
import com.unifor.br.server_primary.repository.UserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private final List<String> replicas = Arrays.asList(
            "http://localhost:8081/replicate",
            "http://localhost:8082/replicate"
    );

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void salvarEReplicar(User user) {
        User userSalvo = userRepository.save(user);
        System.out.println("Dado salvo localmente. Iniciando replicação para " + userSalvo.getName() + "...");

        // 1. Criamos a "etiqueta" garantindo que é um JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> requestEntity = new HttpEntity<>(userSalvo, headers);

        // 2. Disparamos para as réplicas
        for (String url : replicas) {
            try {
                restTemplate.postForEntity(url, requestEntity, String.class);
                System.out.println("✅ Replicado com sucesso para: " + url);
            } catch (Exception e) {
                System.out.println("⚠️ Falha ao replicar para " + url);
                // 👇 O X DA QUESTÃO: Agora ele vai nos dizer o motivo exato!
                System.out.println("   -> Motivo real do erro: " + e.getMessage());
            }
        }
    }
}