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
     * Почитал, что в скором времени RestTemplate станет устаревшим(уже стал по сути), в новых
     * проектах будет активно использоваться именно WebClient
     * Шаблон же вроде как это не четкое требования к ТЗ, а просто пример как можно.
     * Надеюсь на понимание :)
     *
     * пс, я раньше не работал с веб клиентом(кроме тестов), да и время поджимает, поэтому скорее
     * всего
     * реализации немного будет корявой, если увидите недочеты и как можно исправить
     * направьте на верный путьЗ)*/
    private final WebClient webClient;

    public BaseClient(String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Метод для получения одного объекта из другого сервиса
     *
     * @param path         путь до ресурса
     * @param userId       id пользователя
     * @param parameters   параметры запроса
     * @param responseType тип возвращаемого объекта
     * @param <V>          тип возвращаемого объекта
     * @return объект типа V
     */
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

    /**
     * Метод для получения списка объектов из другого сервиса с указанием пользователя
     *
     * @param path         путь до ресурса
     * @param userId       id пользователя
     * @param parameters   параметры запроса
     * @param responseType тип возвращаемого объекта
     * @param <V>          тип возвращаемого объекта
     * @return список объектов типа V
     */
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

    /**
     * Метод для получения списка объектов из другого сервиса без указания пользователя
     *
     * @param path         путь до ресурса
     * @param parameters   параметры запроса
     * @param responseType тип возвращаемого объекта
     * @param <V>          тип возвращаемого объекта
     * @return список объектов типа V
     */
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

    /**
     * Метод для отправки POST запроса с указанием пользователя
     *
     * @param path         путь до ресурса
     * @param body         тело запроса
     * @param userId       id пользователя
     * @param responseType тип возвращаемого объекта
     * @param <T>          тип тела запроса
     * @param <V>          тип возвращаемого объекта
     * @return объект типа V
     */
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

    /**
     * Метод для отправки POST запроса без указания пользователя
     *
     * @param path         путь до ресурса
     * @param body         тело запроса
     * @param responseType тип возвращаемого объекта
     * @param <T>          тип тела запроса
     * @param <V>          тип возвращаемого объекта
     * @return объект типа V
     */
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

    /**
     * Метод для отправки PATCH запроса с указанием пользователя
     *
     * @param path         путь до ресурса
     * @param body         тело запроса
     * @param userId       id пользователя
     * @param responseType тип возвращаемого объекта
     * @param <V>          тип возвращаемого объекта
     * @return
     */
    protected <V> ResponseEntity<V> patch(String path,
                                          Object body,
                                          Long userId,
                                          Class<V> responseType) {
        WebClient.RequestBodySpec uriSpec = webClient.patch()
                .uri(path)
                .header("X-Sharer-User-Id", userId.toString());

        WebClient.ResponseSpec responseSpec;
        if (body != null) {
            responseSpec = uriSpec.bodyValue(body)
                    .retrieve();
        } else {
            responseSpec = uriSpec.retrieve();
        }

        return responseSpec.toEntity(responseType)
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
