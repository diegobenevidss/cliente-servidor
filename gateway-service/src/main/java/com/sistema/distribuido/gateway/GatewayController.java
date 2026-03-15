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

        // 👇 O SEGREDO ESTÁ AQUI: Criamos a "etiqueta" avisando que é um JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

        try {
            System.out.println("Encaminhando para o líder: " + urlLider);
            // Repassamos a requestEntity (que tem o texto + a etiqueta JSON)
            return restTemplate.postForEntity(urlLider + "/save", requestEntity, String.class);
        } catch (Exception e) {
            System.out.println("Falha no líder " + urlLider + ". Motivo: " + e.getMessage());
            System.out.println("Iniciando failover...");

            electionService.realizarEleicao();
            String novoLider = electionService.getLiderAtual();

            try {
                return restTemplate.postForEntity(novoLider + "/save", requestEntity, String.class);
            } catch (Exception ex) {
                return ResponseEntity.status(503).body("Erro Crítico: Nenhum servidor disponível.");
            }
        }
    }
}