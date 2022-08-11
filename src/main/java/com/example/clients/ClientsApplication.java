package com.example.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClientInterceptor;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@Slf4j
public class ClientsApplication {

  private static final String apiKey = "b05ae103-4a78-43c3-b0af-a09945a6d530";

  static class OperaWebSocketGraphQlClientInterceptor implements WebSocketGraphQlClientInterceptor {
    public Mono<Object> connectionInitPayload() {
      final String oAuthToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6ImNMQXpEelN1dDc2VFZwYWxnMUw3Tll3TGZQOCIsImtpZCI6Im1zLW9hdXRoa2V5In0.eyJzdWIiOiJXSEJPQzAwMV9BV1MtTVMtU0EtQ1JFRDI0MCIsImlzcyI6Ind3dy5vcmFjbGUuY29tIiwib3JhY2xlLm9hdXRoLnN2Y19wX24iOiJPQXV0aFNlcnZpY2VQcm9maWxlIiwiaWF0IjoxNjYwMjI0OTY3LCJvcmFjbGUub2F1dGgucHJuLmlkX3R5cGUiOiJMREFQX1VJRCIsImV4cCI6MTY2MDIyODU2Nywib3JhY2xlLm9hdXRoLnRrX2NvbnRleHQiOiJ1c2VyX2Fzc2VydGlvbiIsImF1ZCI6WyJodHRwczovLypvcmFjbGUqLmNvbSIsImh0dHBzOi8vKi5pbnQgIiwiaHR0cHM6Ly8qb2NzLm9jLXRlc3QuY29tLyJdLCJwcm4iOiJXSEJPQzAwMV9BV1MtTVMtU0EtQ1JFRDI0MCIsImp0aSI6IjBiMDc2NzQyLTY0ZjYtNDRjMi1hN2ExLWVmNjBhNTgxMmVjYiIsIm9yYWNsZS5vYXV0aC5jbGllbnRfb3JpZ2luX2lkIjoiV0hCT0MwMDFfQ2xpZW50IiwidXNlci50ZW5hbnQubmFtZSI6IkRlZmF1bHREb21haW4iLCJvcmFjbGUub2F1dGguaWRfZF9pZCI6IjEyMzQ1Njc4LTEyMzQtMTIzNC0xMjM0LTEyMzQ1Njc4OTAxMiJ9.HB942aDCS2Fh91rctM6OL50Z7Xo_MkPTsP3p0a9vN8XMlDh1U5TYRmV1-_vJTyU2rYjZmlGbg4f8Cylpnm_ggpvCfa_gSn7raSvzVOwjJYT-VBHU1L7jonG6GY5VLRVFZAVgK6ZMrOO5CdmJNZPF-99IyXD6aFN5spQbvF-WdbRSuVU-FKqxtvuagvsSvEnZsJqPsGKZkWMXGl1k4Be6oz5VfTmhgdpmydE4HFmu7cC7U4u0yP_s7wQBuX3Bgyh0Z-Nhhiyof6rJ8PpslWtYPWPzdbKQXd3E-IRYGXqcr8BTzeO0rFYlfXC8rjxvYBtwG83AAKnsNkA-bUiEqsWEYw";
      final InitMessage initMessage = getInitMessage(oAuthToken, apiKey);
      return Mono.just(initMessage.getPayload());
    }
  }

    public static void main(String[] args) {
        SpringApplication.run(ClientsApplication.class, args);
    }

    @Bean
  WebSocketGraphQlClient webSockerGraphQLClient(){

      //final String hashedApiKey = HashingUtils.getSHA256Hash(apiKey);
      final String hashedApiKey = "ee790a966704eba4deb4c07450fb99f132261986095b6f54ff31fae7dcf60e6d";
      log.info("hashedApiKey: {}", hashedApiKey);
      final String url =
          "wss://whitbce4ua.hospitality-api.eu-frankfurt-1.ocs.oc-test.com/subscriptions?key="
              +hashedApiKey;
      //String url = "ws://127.0.0.1:8080/subscriptions";
      System.out.println("websocket connection url:"+url);
      WebSocketClient client = new ReactorNettyWebSocketClient();
      /**WebSocketGraphQlClient graphQlClient = WebSocketGraphQlClient
          .builder(url, client).build(); **/

      WebSocketGraphQlClient graphQlClient = WebSocketGraphQlClient.builder(url, client)
          .interceptor(new OperaWebSocketGraphQlClientInterceptor())
          .headers(httpHeaders -> {
            httpHeaders.set("Sec-WebSocket-Protocol", "graphql-transport-ws");
          }).build();

      return graphQlClient;
    }

  private void makeInitCall(
      final WebSocketGraphQlClient webSocketGraphQlClient,
      final String initMessage){
    webSocketGraphQlClient
        .document(initMessage)
        .execute()
        .map(response -> {
          if (!response.isValid()) {
            log.error("Received Error Msg, while sending init message {}",
                response.getErrors());
          }
          log.info("Response from Opera : {}", response.toEntity(String.class));
          return response.toEntity(String.class);
        }).subscribe();
  }

  private void makeSubscriptionCall(
      final WebSocketGraphQlClient webSocketGraphQlClient,
      final String subscriptionQuery){

    final ObjectMapper objectMapper = new ObjectMapper();

    Flux<EventHeader> result = webSocketGraphQlClient.document(subscriptionQuery)
        //.variables(createSubscriptionVariables)
        .executeSubscription()
        .onErrorStop()
        .map(response -> {
          if(!response.isValid()) {
            log.error("Received Error Msg: {}", response.getErrors());
          }
          return objectMapper.convertValue(response.<Map<String, Object>>getData().get("newEvent"), EventHeader.class);
        });

    result.log().subscribe();
  }

  private static InitMessage getInitMessage(final String oAuthToken, final String apiKey) {

    final String bearerAuthToken = "Bearer "+ oAuthToken;

    final Payload payload =
        Payload.builder().xAppKey(apiKey).authorization(bearerAuthToken).build();

    final InitMessage initMessage =
        InitMessage.builder().type("connection_init").payload(payload).build();
    return initMessage;
  }

  private String convertToJsonString(final Object object){
    // Create ObjectMapper object.
    String result = null;
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);

    try {
      result = mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return result;
  }


	@Bean
	ApplicationRunner applicationRunner (WebSocketGraphQlClient graphQlClient){
		return args -> {
      final String oAuthToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6ImNMQXpEelN1dDc2VFZwYWxnMUw3Tll3TGZQOCIsImtpZCI6Im1zLW9hdXRoa2V5In0.eyJzdWIiOiJXSEJPQzAwMV9BV1MtTVMtU0EtQ1JFRDI0MCIsImlzcyI6Ind3dy5vcmFjbGUuY29tIiwib3JhY2xlLm9hdXRoLnN2Y19wX24iOiJPQXV0aFNlcnZpY2VQcm9maWxlIiwiaWF0IjoxNjYwMjE4NTMzLCJvcmFjbGUub2F1dGgucHJuLmlkX3R5cGUiOiJMREFQX1VJRCIsImV4cCI6MTY2MDIyMjEzMywib3JhY2xlLm9hdXRoLnRrX2NvbnRleHQiOiJ1c2VyX2Fzc2VydGlvbiIsImF1ZCI6WyJodHRwczovLypvcmFjbGUqLmNvbSIsImh0dHBzOi8vKi5pbnQgIiwiaHR0cHM6Ly8qb2NzLm9jLXRlc3QuY29tLyJdLCJwcm4iOiJXSEJPQzAwMV9BV1MtTVMtU0EtQ1JFRDI0MCIsImp0aSI6IjVlYThmYWUzLWM0OTUtNGQ1Yy1hNzQzLWM1MTM4ZDI4ZGJjMiIsIm9yYWNsZS5vYXV0aC5jbGllbnRfb3JpZ2luX2lkIjoiV0hCT0MwMDFfQ2xpZW50IiwidXNlci50ZW5hbnQubmFtZSI6IkRlZmF1bHREb21haW4iLCJvcmFjbGUub2F1dGguaWRfZF9pZCI6IjEyMzQ1Njc4LTEyMzQtMTIzNC0xMjM0LTEyMzQ1Njc4OTAxMiJ9.Yub7JUPf0BqDi58LmFvMczyuS3FAxXIhlaDI9G_b7vJ_MGhyE_MMBSSSiGU5ZQOvqPzbP0aMSqAcGwIj1K7rd3rVM8uK1lfmWfhuRpj3cQ8OZt5JGmSMpMgInaMbhkebIV-owFDAENDu-J0m3fmpP3he9c_kgptwecRMUoRz9Pn4BZX9WBG4aH9XgILwe2RE5wHw0Kmj5bXTefWppgmZ8WvwSatyyAcb1TYUBsSA-wmEGlv-UEE4sUQsNNhVkbBal3COwXra5uPaJ5YMWNx_Y33dT5D-mkG48VHJ2AysoVrg4_qX2sseOy4c45KBjzwz_HY0PMGweqK1ru02KFDjYA";

      //final InitMessage initMessage = getInitMessage(oAuthToken, apiKey);

     // final String initMessageJson = convertToJsonString(initMessage);
      //log.info("initMessageJson: {}", initMessageJson);

      //makeInitCall(graphQlClient, initMessageJson);

      graphQlClient.start();

      /**var subscriptionReqDock = "{\"id\":\"1\",\"type\":\"subscribe\",\"payload\":{\"query\":\"subscription {\\n   newEvent (input:{chainCode: \\\"WHBOC001\\\", offset: \\\"50\\\"}) {\\n     metadata {\\n       offset\\n       uniqueEventId\\n     }\\n     moduleName\\n     eventName\\n     detail {\\n       elementName\\n       newValue\\n       oldValue\\n     }\\n   }\\n }\",\"variables\":null}}";



      makeSubscriptionCall(graphQlClient, subscriptionReqDock); **/


      var subscriptionQuery = "subscription onNewEvent($input: NewEventInput!) { \n"
          + "newEvent(input:$input) { "
          + "metadata {\n"
          + "  offset\n"
          + "  uniqueEventId\n"
          + "}\n"
          + "moduleName\n"
          + "eventName\n"
          + "hotelId\n"
          + "primaryKey\n"
          + "detail {\n"
          + " elementName\n"
          + " newValue\n"
          + " oldValue\n"
          + "} } }".trim();
      Map<String, Object> createSubscriptionVariables = Map.of(
          "input", Map.of(
              "chainCode", "WHBOC001"
              //"offset", "50",
              //"delta", true
          )
      );

      final ObjectMapper objectMapper = new ObjectMapper();

      Flux<EventHeader> result = graphQlClient.document(subscriptionQuery)
          .variables(createSubscriptionVariables)
          .executeSubscription()
          .onErrorStop()
          .map(response -> {
            if(!response.isValid()) {
              log.error("Received Error Msg: {}", response.getErrors());
            }
            return objectMapper.convertValue(response.<Map<String, Object>>getData().get("newEvent"), EventHeader.class);
          });

      result.log().subscribe();

    };
	}
}