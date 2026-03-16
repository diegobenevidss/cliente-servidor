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
        System.out.println("   CLIENTE - SISTEMA DISTRIBUÍDO (H2)    ");
        System.out.println("==========================================");

        // O loop agora começa AQUI, pedindo o nome toda vez!
        while (true) {
            System.out.print("\nDigite o NOME (ou 'sair' para encerrar) > ");
            String name = scanner.nextLine();

            // A condição de saída agora fica no nome
            if (name.equalsIgnoreCase("sair")) {
                System.out.println("Encerrando o cliente...");
                break;
            }

            System.out.print("Digite o EMAIL > ");
            String email = scanner.nextLine();

            // 🛡️ A trava de segurança contra "dedos gordos"
            if (!email.contains("@")) {
                System.out.println("⚠️ E-mail inválido! Um e-mail precisa ter o símbolo '@'. Tente novamente.");
                continue; // Pula o envio e volta pro começo do loop
            }

            // Monta o JSON
            String jsonPayload = String.format("{\"name\": \"%s\", \"email\": \"%s\"}", name, email);

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(GATEWAY_URL))
                        .header("Content-Type", "application/json") // Avisa que é JSON
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    System.out.println("✅ Sucesso: " + response.body());
                } else {
                    System.out.println("⚠️ Erro: Status " + response.statusCode());
                }
            } catch (Exception e) {
                System.out.println("❌ Erro de Conexão: O Gateway está offline ou a URL está errada!");
            }
        }
        scanner.close();
    }
}