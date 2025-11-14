package com.gym_membership.gym_membership_backend.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym_membership.gym_membership_backend.ai.dto.AiRecommendRequest;
import com.gym_membership.gym_membership_backend.ai.dto.AiRecommendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.url}")
    private String baseUrl; // ex: https://generativelanguage.googleapis.com

    @Value("${gemini.api.key}")
    private String apiKey;

    // MODIFICAT: Am actualizat la modelul care știm că funcționează
    @Value("${gemini.model:gemini-2.5-flash}")
    private String model;

    @Value("${gemini.timeout.seconds:40}")
    private int timeoutSeconds;

    @Value("${gemini.api.version:v1beta}")
    private String apiVersion; // v1 pentru cheia din Cloud Console

    private static final String GENERATE_SUFFIX = ":generateContent";

    private WebClient buildClient() {
        HttpClient http = HttpClient.create().responseTimeout(Duration.ofSeconds(timeoutSeconds));
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(http))
                .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    /** Apel sincron care trimite promptul la Gemini și întoarce recomandarea. */
    public AiRecommendResponse recommend(AiRecommendRequest req) {
        // Prompturi
        String systemPrompt = """
            You are an assistant for a gym admin app.
            Pick ONE membership type (by id) and ONE trainer (by id) that best match the member's goal, experience, budget and preferred schedule.
            Return STRICT JSON only:
            { "membershipTypeId": number|null, "trainerId": number|null, "rationale": "short explanation" }.
            If nothing fits, set the id(s) to null and explain briefly.
            """;

        String userPrompt = """
            Member: %s
            Goal: %s
            Experience: %s
            Budget tier: %s
            Schedule: %s

            Trainers (id, name, specialties): %s
            MembershipTypes (id, type, price): %s
            """.formatted(
                nullToDash(req.memberName()),
                nullToDash(req.goal()),
                nullToDash(req.experience()),
                nullToDash(req.budgetTier()),
                nullToDash(req.schedule()),
                safeToString(req.trainers()),
                safeToString(req.membershipTypes())
        );

        // v1: fara response_mime_type (nu e suportat la generateContent)
        Map<String, Object> body = Map.of(
                "generation_config", Map.of(
                        "temperature", 0.3
                ),
                "contents", new Object[] {
                        Map.of(
                                "role", "user",
                                "parts", new Object[] {
                                        Map.of("text",
                                                systemPrompt +
                                                        "\n\n" + userPrompt +
                                                        "\n\nReturn ONLY a valid JSON object with keys membershipTypeId, trainerId, rationale."
                                        )
                                }
                        )
                }
        );

        try {
            WebClient client = buildClient();
            String path = "/" + apiVersion + "/models/" + model + GENERATE_SUFFIX;

            String raw = client.post()
                    .uri(path)
                    .header("x-goog-api-key", apiKey)           // cheia în header
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(
                            s -> s.is4xxClientError() || s.is5xxServerError(),
                            resp -> {
                                // -------- ÎNCEPUT MODIFICARE --------
                                // Dacă e 503, aruncăm excepția noastră specială
                                if (resp.statusCode().value() == 503) {
                                    return resp.bodyToMono(String.class)
                                            .flatMap(err -> Mono.error(new ServiceOverloadedException(err)));
                                }
                                // Pentru orice altă eroare, aruncăm excepția normală
                                return resp.bodyToMono(String.class)
                                        .flatMap(err -> Mono.error(
                                                new RuntimeException("Gemini HTTP " + resp.statusCode() + " — " + err)));
                                // -------- SFÂRȘIT MODIFICARE --------
                            }
                    )
                    .bodyToMono(String.class)
                    // -------- ÎNCEPUT MODIFICARE --------
                    // Aici adăugăm logica de REÎNCERCARE (retry)
                    .retryWhen(Retry.backoff(4, Duration.ofSeconds(2))
                            .filter(throwable -> throwable instanceof ServiceOverloadedException) // Doar dacă e eroarea noastră 503
                            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                    // Dacă tot eșuează după 3 încercări, aruncăm eroarea finală
                                    new RuntimeException("Model is still overloaded after 3 retries.", retrySignal.failure())
                            )
                    )
                    // -------- SFÂRȘIT MODIFICARE --------
                    .block(Duration.ofSeconds(timeoutSeconds + 5));

            if (raw == null || raw.isBlank()) {
                return new AiRecommendResponse(null, null, "Empty response from Gemini");
            }

            // Parse răspuns (candidates[0].content.parts[0].text conține JSON-ul nostru)
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(raw);
            JsonNode txtNode = root.path("candidates")
                    .path(0).path("content").path("parts")
                    .path(0).path("text");

            String json = txtNode.isTextual() ? txtNode.asText() : raw;

            // Curățăm JSON-ul de Markdown (```json ... ```)
            String cleanedJson = cleanGeminiResponse(json);

            try {
                // Folosim "cleanedJson"
                return mapper.readValue(cleanedJson, AiRecommendResponse.class);
            } catch (Exception parse) {
                log.warn("Gemini JSON parse failed. Raw: {}", cleanedJson);
                return new AiRecommendResponse(null, null,
                        "AI response parsing failed. Raw: " + cleanedJson);
            }

        } catch (Exception ex) {
            log.error("Gemini call failed", ex);
            String msg = ex.getClass().getSimpleName() +
                    (ex.getMessage() != null ? (": " + ex.getMessage()) : "");
            return new AiRecommendResponse(null, null, "Gemini call failed: " + msg);
        }
    }

    private static String nullToDash(String s) { return (s == null || s.isBlank()) ? "-" : s; }
    private static String safeToString(Object o) { return (o == null) ? "[]" : o.toString(); }

    // -------- ÎNCEPUT MODIFICARE --------
    /**
     * NOU: Curăță răspunsul de la Gemini, eliminând blocurile de Markdown (```json ... ```)
     * și extrăgând doar obiectul JSON.
     */
    private String cleanGeminiResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return rawResponse;
        }

        // Caută primul '{'
        int firstBrace = rawResponse.indexOf('{');
        // Caută ultimul '}'
        int lastBrace = rawResponse.lastIndexOf('}');

        // Dacă găsim un JSON valid în interior
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return rawResponse.substring(firstBrace, lastBrace + 1);
        }

        // Dacă nu, returnăm textul original (care va eșua din nou, dar e ok)
        return rawResponse;
    }

    /** Excepție custom pentru a semnala o eroare 503 care poate fi reîncercată */
    private static class ServiceOverloadedException extends RuntimeException {
        public ServiceOverloadedException(String message) {
            super(message);
        }
    }

}