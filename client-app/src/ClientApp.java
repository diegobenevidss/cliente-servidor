import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ClientApp {
    private static final String GATEWAY_URL = "http://localhost:8000/enviar";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HttpClient client = HttpClient.newHttpClient();

        System.out.println("==========================================");
        System.out.println("   CLIENTE - SISTEMA DISTRIBUÍDO (P2P)   ");
        System.out.println("==========================================");
        System.out.println("Conectado ao Gateway: " + GATEWAY_URL);
        System.out.println("Digite sua mensagem e pressione Enter (ou 'sair' para encerrar):");

        while (true) {
            System.out.print("\nSua mensagem > ");
            String mensagem = scanner.nextLine();

            if (mensagem.equalsIgnoreCase("sair")) {
                System.out.println("Encerrando cliente...");
                break;
            }

            try {
                // Monta a requisição POST para o Gateway
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(GATEWAY_URL))
                        .header("Content-Type", "text/plain")
                        .POST(HttpRequest.BodyPublishers.ofString(mensagem))
                        .build();

                // Envia e aguarda a resposta
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    System.out.println("✅ Sucesso: " + response.body());
                } else {
                    System.out.println("⚠️ Erro no Servidor: Status " + response.statusCode());
                }

            } catch (Exception e) {
                System.out.println("❌ Erro de Conexão: O Gateway está offline!");
            }
        }
        scanner.close();
    }
}