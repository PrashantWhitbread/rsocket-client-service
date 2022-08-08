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
import uk.co.whitbread.ohipmockserver.domain.hotel.availability.output.EventHeader;

@SpringBootApplication
@Slf4j
public class ClientsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientsApplication.class, args);
    }

    @Bean
  WebSocketGraphQlClient webSockerGraphQLClient(){
      //String url = "ws://127.0.0.1:8080/subscriptions";
      //String url = "ws://127.0.0.1:8080/subscriptions";
      String url = "wss://whitbce4ua.hospitality-api.eu-frankfurt-1.ocs.oc-test.com";
      System.out.println("websocket connection url:"+url);
      final String oAuthToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6ImNMQXpEelN1dDc2VFZwYWxnMUw3Tll3TGZQOCIsImtpZCI6Im1zLW9hdXRoa2V5In0.eyJzdWIiOiJXSEJPQzAwMV9BV1MtTVMtU0EtQ1JFRDI0MCIsImlzcyI6Ind3dy5vcmFjbGUuY29tIiwib3JhY2xlLm9hdXRoLnN2Y19wX24iOiJPQXV0aFNlcnZpY2VQcm9maWxlIiwiaWF0IjoxNjU5OTUxMzU1LCJvcmFjbGUub2F1dGgucHJuLmlkX3R5cGUiOiJMREFQX1VJRCIsImV4cCI6MTY1OTk1NDk1NSwib3JhY2xlLm9hdXRoLnRrX2NvbnRleHQiOiJ1c2VyX2Fzc2VydGlvbiIsImF1ZCI6WyJodHRwczovLypvcmFjbGUqLmNvbSIsImh0dHBzOi8vKi5pbnQgIiwiaHR0cHM6Ly8qb2NzLm9jLXRlc3QuY29tLyJdLCJwcm4iOiJXSEJPQzAwMV9BV1MtTVMtU0EtQ1JFRDI0MCIsImp0aSI6IjlhYThmNWRjLTAwMjItNGM0ZS05NDJmLTc1ZTU0MjI2ZjM4OCIsIm9yYWNsZS5vYXV0aC5jbGllbnRfb3JpZ2luX2lkIjoiV0hCT0MwMDFfQ2xpZW50IiwidXNlci50ZW5hbnQubmFtZSI6IkRlZmF1bHREb21haW4iLCJvcmFjbGUub2F1dGguaWRfZF9pZCI6IjEyMzQ1Njc4LTEyMzQtMTIzNC0xMjM0LTEyMzQ1Njc4OTAxMiJ9.GcLzUtKcRJBIEGQJmYJlozzbmd4zMTj-m1YDJhmUI3lnPUxGdOBbA7uIankgzfbG0Z03tgaR19yYLqKpsbxPJi56lwtJRH6HqCXeUwA-9JlULS_Bs9ObegS0udtTH1bW327i_qYWSa96Y3Ix_F6vQ2uOZNVfD5_KaxxKwDhDQS7z94Plt-cCpUm9jxj3fISCdR8fJmrdLtbRWXPYqPiIhS_eQPmxzwotfgB2rcidd9RsBFzh5vaoyCJwJFu-GMBMJae4KGQZmoWYaolfqm2VgrGve3Fwdv4hibR1v8gypFUOQCsylHtnNM0mHmIG0_Pcp_PPEdH8Oy5lOCqu4DUyUw";
      final String apiKey = "99066452-d5ae-4f4b-8476-244c5fece415";
      final String socketKey = "89b45884cdb716d24c50c2800ea2463b76e61aeb1af41e08bc18f8cda92325b7";

      WebSocketClient client = new ReactorNettyWebSocketClient();
      WebSocketGraphQlClient graphQlClient = WebSocketGraphQlClient
          .builder(url, client)
          .headers(httpHeaders -> {
        //httpHeaders.setBearerAuth(oAuthToken);
        httpHeaders.set("key", socketKey);
      }).build();
      return graphQlClient;
    }



	@Bean
	ApplicationRunner applicationRunner (WebSocketGraphQlClient graphQlClient){
		return args -> {

      graphQlClient.start().subscribe();

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
              "offset", "test123",
              "delta", true
          )
      );

      final ObjectMapper objectMapper = new ObjectMapper();

      Flux<EventHeader> result = graphQlClient.document(subscriptionQuery).variables(createSubscriptionVariables)
          .executeSubscription()
          .map(response -> {
            if(!response.isValid()) {
              log.error("Received Error Msg: {}", response.getErrors());
            }
            return objectMapper.convertValue(response.<Map<String, Object>>getData().get("newEvent"), EventHeader.class);
          });

    };
	}
}