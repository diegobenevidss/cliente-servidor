package com.sistema.distribuido.gateway;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class GatewayController {

    private final ElectionService electionService;
    private final RestTemplate restTemplate = new RestTemplate();

    public GatewayController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<String> encaminharMensagem(@RequestBody String jsonPayload) {
        String urlLider = electionService.getLiderAtual();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

        try {
            // Tenta mandar pro Primário (na rota /save)
            if (urlLider.contains("8080")) {
                return restTemplate.postForEntity(urlLider + "/save", requestEntity, String.class);
            } else {
                // Se o líder for a Réplica (8081), manda na rota /replicate dela
                return restTemplate.postForEntity(urlLider + "/replicate", requestEntity, String.class);
            }
        } catch (Exception e) {
            System.out.println("Falha no líder " + urlLider + ". Motivo: " + e.getMessage());
            System.out.println("Iniciando failover...");

            electionService.realizarEleicao();
            String novoLider = electionService.getLiderAtual();

            try {
                // Dispara para a nova líder eleita
                return restTemplate.postForEntity(novoLider + "/replicate", requestEntity, String.class);
            } catch (Exception ex) {
                return ResponseEntity.status(503).body("Erro Crítico: Nenhum servidor disponível.");
            }
        }
    }
}