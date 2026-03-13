package com.sistema.distribuido.gateway;

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

    // O Gateway recebe o JSON como uma String pura para evitar dependências de Models
    @PostMapping("/enviar")
    public ResponseEntity<String> encaminharMensagem(@RequestBody String jsonPayload) {
        String urlLider = electionService.getLiderAtual();

        try {
            System.out.println("Encaminhando para o líder: " + urlLider);
            return restTemplate.postForEntity(urlLider + "/save", jsonPayload, String.class);
        } catch (Exception e) {
            System.out.println("Falha no líder " + urlLider + ". Iniciando failover...");

            electionService.realizarEleicao(); // Elege a réplica 1
            String novoLider = electionService.getLiderAtual();

            try {
                return restTemplate.postForEntity(novoLider + "/save", jsonPayload, String.class);
            } catch (Exception ex) {
                return ResponseEntity.status(503).body("Erro Crítico: Nenhum servidor disponível.");
            }
        }
    }
}