package com.example.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;

@SpringBootApplication
@Slf4j
public class ClientsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientsApplication.class, args);
    }

    @Bean
  WebSocketGraphQlClient webSockerGraphQLClient(){
      String url = "ws://127.0.0.1:8080/subscriptions";
      System.out.println("websocket connection url:"+url);
      WebSocketClient client = new ReactorNettyWebSocketClient();
      WebSocketGraphQlClient graphQlClient = WebSocketGraphQlClient
          .builder(url, client).build();
      return graphQlClient;
    }



	@Bean
	ApplicationRunner applicationRunner (WebSocketGraphQlClient graphQlClient){
		return args -> {

      graphQlClient.start();

      var subscriptionQuery = "subscription onNewEvent($subscriptionInput: NewEventInput!) { \n"
          + "newEvent(subscriptionInput:$subscriptionInput) { "
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
          "subscriptionInput", Map.of(
              "chainCode", "WHBOC001",
              "offset", "50",
              "delta", true
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