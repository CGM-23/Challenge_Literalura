package com.example.Literalura.servicios;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class ConsumoApi {
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL) // 👈 sigue 301/302/307/308 en GET
            .build();

    public String obtenerDatos(String url) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(20))

                .GET()
                .build();

        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            int status = response.statusCode();
            if (status < 200 || status >= 300) {
                throw new RuntimeException("HTTP " + status + " en " + url + " — cuerpo: " + response.body());
            }
            String body = response.body();
            if (body == null || body.isBlank()) {
                throw new RuntimeException("Respuesta vacía desde " + url);
            }
            return body;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Llamada interrumpida: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Error E/S al llamar " + url + ": " + e.getMessage(), e);
        }
    }
}
