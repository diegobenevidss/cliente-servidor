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

        System.out.print("Digite o seu nome de usuário: ");
        String nome = scanner.nextLine();

        while (true) {
            System.out.print("\nSua mensagem > ");
            String mensagem = scanner.nextLine();

            if (mensagem.equalsIgnoreCase("sair")) break;

            // Monta o JSON manualmente no formato da classe User
            String jsonPayload = String.format("{\"nome\": \"%s\", \"mensagem\": \"%s\"}", nome, mensagem);

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
                System.out.println("❌ Erro de Conexão: O Gateway está offline!");
            }
        }
        scanner.close();
    }
}