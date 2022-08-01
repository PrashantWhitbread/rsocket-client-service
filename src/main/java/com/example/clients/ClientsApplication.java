package com.example.clients;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ClientsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientsApplication.class, args);
    }

   /** @Bean
    HttpGraphQlClient httpGraphQlClient() {
        return HttpGraphQlClient.builder().url("http://127.0.0.1:8080/graphql").build();
    } **/

    /**@Bean
    RSocketGraphQlClient rSocketGraphQlClient(RSocketGraphQlClient.Builder<?> builder) {
        return builder.tcp("127.0.0.1", 9191).route("graphql").build();
    } **/

    @Bean
  WebSocketGraphQlClient webSockerGraphQLClient(){
      String url = "ws://127.0.0.1:8080/subscriptions";
      WebSocketClient client = new ReactorNettyWebSocketClient();
      WebSocketGraphQlClient graphQlClient = WebSocketGraphQlClient.builder(url, client).build();
      return graphQlClient;
    }



	@Bean
	ApplicationRunner applicationRunner (WebSocketGraphQlClient websocket){
		return args -> {

			/*var httpRequestDocument = """
					
					query {
					 customerById(id:1){ 
					  id, name
					 }
					}
					
					""" ;*/
			/**String httpRequestDocument = "{ greeting { greeting } } "; **/

			/*http.document(httpRequestDocument).retrieve("customerById").toEntity(Customer.class)
					.subscribe(System.out::println);*/
			/**http.document(httpRequestDocument).retrieve("greeting").toEntity(Greeting.class)
					.subscribe(System.out::println); **/


			/**String rsocketRequestDocument = " subscription { greetings { greeting } } " ;
			rsocket.document(rsocketRequestDocument)
					.retrieveSubscription("greetings")
					.toEntity(Greeting.class)
					.subscribe(System.out::println); **/


      String webSocketRequestDocument = "subscription { greetings } " ;
      /**websocket.document(webSocketRequestDocument)
          .retrieveSubscription("greeting")
          .toEntity(String.class)
          .subscribe(System.out::println); **/
      /*Flux<String> greetingFlux = websocket.document("subscription { greetings } ")
          .retrieveSubscription("greetings")
          .toEntity(String.class);
      greetingFlux.subscribe(System.out::println);*/

      var mockServerReqDoc = "subscription {\n"
          + "\t\t\t\t\t      hotelAvailability {\n"
          + "\t\t\t\t\t          businessEventData {\n"
          + "\t\t\t\t\t              businessEvent {\n"
          + "\t\t\t\t\t                  header {\n"
          + "\t\t\t\t\t                      moduleName,\n"
          + "\t\t\t\t\t                      actionType,\n"
          + "\t\t\t\t\t                      actionId,\n"
          + "\t\t\t\t\t                      primaryKey,\n"
          + "\t\t\t\t\t                      publisherId,\n"
          + "\t\t\t\t\t                      createdDateTime,\n"
          + "\t\t\t\t\t                      hotelId,\n"
          + "\t\t\t\t\t                  },\n"
          + "\t\t\t\t\t                  details {\n"
          + "\t\t\t\t\t                      businessEventDetails{\n"
          + "\t\t\t\t\t                          dataElement,\n"
          + "\t\t\t\t\t                          newValue,\n"
          + "\t\t\t\t\t                          oldValue,\n"
          + "\t\t\t\t\t                      }\n"
          + "\t\t\t\t\t                  }\n"
          + "\t\t\t\t\t              },\n"
          + "\t\t\t\t\t              businessEventId {\n"
          + "\t\t\t\t\t                  id,\n"
          + "\t\t\t\t\t              }\n"
          + "\t\t\t\t\t          }\n"
          + "\t\t\t\t\t      }\n"
          + "\t\t\t\t\t  }";

      Flux<String> greetingFlux = websocket.document(mockServerReqDoc)
          .retrieveSubscription("hotelAvailability")
          .toEntity(String.class);
      greetingFlux.subscribe(System.out::println);
		};


	}
}