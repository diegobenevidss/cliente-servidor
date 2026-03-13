package com.sistema.distribuido.gateway;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Service
public class ElectionService {

    // Ordem de prioridade estática
    private final List<String> servidores = Arrays.asList(
            "http://localhost:8080", // Prioridade 1 (Primário)
            "http://localhost:8081", // Prioridade 2 (Réplica 1)
            "http://localhost:8082"  // Prioridade 3 (Réplica 2)
    );

    private String liderAtual = servidores.get(0);
    private final RestTemplate restTemplate = new RestTemplate();

    public String getLiderAtual() {
        return liderAtual;
    }

    public void realizarEleicao() {
        System.out.println("⚠️ Iniciando eleição de novo líder...");

        for (String url : servidores) {
            if (verificarSaude(url)) {
                this.liderAtual = url;
                System.out.println("👑 Novo líder eleito: " + url);
                return;
            }
        }
        System.err.println("❌ CRÍTICO: Nenhum servidor disponível!");
    }

    private boolean verificarSaude(String url) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url + "/health", String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}