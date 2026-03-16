package com.sistema.distribuido.gateway;

import org.springframework.stereotype.Service;

@Service
public class ElectionService {

    // O padrão é o Primário (8080)
    private String liderAtual = "http://localhost:8080";

    // A nossa réplica de emergência (8081)
    private final String replicaDeEmergencia = "http://localhost:8081";

    public String getLiderAtual() {
        return liderAtual;
    }

    public void realizarEleicao() {
        System.out.println("⚠️ Primário offline! Elegendo Réplica 1 como nova líder...");
        // Muda o ponteiro do Gateway definitivamente para a Réplica 1
        this.liderAtual = replicaDeEmergencia;
    }

    // Método para voltarmos ao normal depois (Teste 4)
    public void restaurarPrimario() {
        this.liderAtual = "http://localhost:8080";
    }
}