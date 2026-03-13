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

    @PostMapping("/enviar")
    public ResponseEntity<String> encaminharMensagem(@RequestBody String mensagem) {
        String urlLider = electionService.getLiderAtual();

        try {
            // Tenta enviar para o líder atual
            return restTemplate.postForEntity(urlLider + "/save", mensagem, String.class);
        } catch (Exception e) {
            System.out.println("Falha ao contactar o líder: " + urlLider);

            // Se falhar, faz uma nova eleição instantânea
            electionService.realizarEleicao();
            String novoLider = electionService.getLiderAtual();

            try {
                // Tenta novamente com o novo líder
                return restTemplate.postForEntity(novoLider + "/save", mensagem, String.class);
            } catch (Exception ex) {
                return ResponseEntity.status(503).body("Erro: Sistema indisponível no momento.");
            }
        }
    }
}