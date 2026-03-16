package com.unifor.br.server_primary.service;

import com.unifor.br.server_primary.model.User;
import com.unifor.br.server_primary.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SyncService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public SyncService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // O pulo do gato: Isso faz o método rodar SOZINHO assim que o servidor liga!
    @EventListener(ApplicationReadyEvent.class)
    public void sincronizarAoLigar() {
        System.out.println("🔄 [Teste 4] Servidor Primário ligando... Buscando histórico com a nova líder (Réplica 1)...");
        try {
            // Faz um GET na campainha da Réplica 1 que lista todos os dados
            ResponseEntity<List<User>> response = restTemplate.exchange(
                    "http://localhost:8081/all-data",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<User>>() {}
            );

            List<User> dadosDaNovaLider = response.getBody();

            if (dadosDaNovaLider != null && !dadosDaNovaLider.isEmpty()) {
                userRepository.deleteAll(); // Limpa o banco velho e desatualizado

                // Salva tudo que baixou
                for (User user : dadosDaNovaLider) {
                    user.setId(null);
                    userRepository.save(user);
                }
                System.out.println("✅ Sincronização concluída! " + dadosDaNovaLider.size() + " registros recuperados da líder.");
                System.out.println("🤖 Assumindo papel de Réplica Subordinada na porta 8080.");
            } else {
                System.out.println("⚠️ O banco da líder está vazio. Nada para sincronizar.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Falha ao buscar dados. A nova líder está online?");
        }
    }
}