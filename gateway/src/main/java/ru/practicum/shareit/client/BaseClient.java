package ru.practicum.shareit.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BaseClient {

    /*В шаблоне был рест темплейт, но я решил поэксперементировать с webclient
     * Почитал, что в скором времени RestTemplate станет устаревшим, в новых проектах будет
     * активно использоваться именно WebClient
     * Шаблон же вроде как это не четкое требования к ТЗ, а просто пример как можно.
     * Надеюсь на понимание :)*/
    private final WebClient webClient;

    public BaseClient(String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    protected <V> ResponseEntity<V> get(String path,
                                        Long userId,
                                        Map<String, String> parameters,
                                        Class<V> responseType) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        parameters.forEach(multiValueMap::add);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(path)
                        .queryParams(multiValueMap)
                        .build())
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .toEntity(responseType)
                .block();
    }

    protected <V> ResponseEntity<List<V>> getAll(String path,
                                                 Long userId,
                                                 Map<String, String> parameters,
                                                 Class<V[]> responseType) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        parameters.forEach(multiValueMap::add);
        V[] responseArray = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(path)
                        .queryParams(multiValueMap)
                        .build())
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .bodyToMono(responseType)
                .block();

        return new ResponseEntity<>(Arrays.asList(responseArray), HttpStatus.OK);
    }

    protected <V> ResponseEntity<List<V>> getAll(String path,
                                                 Map<String, String> parameters,
                                                 Class<V[]> responseType) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        parameters.forEach(multiValueMap::add);
        V[] responseArray = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(path)
                        .queryParams(multiValueMap)
                        .build())
                .retrieve()
                .bodyToMono(responseType)
                .block();

        return new ResponseEntity<>(Arrays.asList(responseArray), HttpStatus.OK);
    }

    protected <T, V> ResponseEntity<V> post(String path,
                                            T body,
                                            Long userId,
                                            Class<V> responseType) {
        return webClient.post()
                .uri(path)
                .header("X-Sharer-User-Id", userId.toString())
                .body(Mono.just(body), body.getClass())
                .retrieve()
                .toEntity(responseType)
                .block();
    }

    protected <T, V> ResponseEntity<V> post(String path,
                                            T body,
                                            Class<V> responseType) {
        return webClient.post()
                .uri(path)
                .body(Mono.just(body), body.getClass())
                .retrieve()
                .toEntity(responseType)
                .block();
    }

    protected <T, V> ResponseEntity<V> patch(String path,
                                             T body,
                                             Long userId,
                                             Class<V> responseType) {
        return webClient.patch()
                .uri(path)
                .header("X-Sharer-User-Id", userId.toString())
                .body(Mono.just(body), body.getClass())
                .retrieve()
                .toEntity(responseType)
                .block();
    }

    protected Mono<ResponseEntity<Object>> delete(String path,
                                                  Long userId) {
        return webClient.delete()
                .uri(path)
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .toEntity(Object.class);
    }
}
