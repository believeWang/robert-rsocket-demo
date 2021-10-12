package com.robert.rsocketclient;

import java.io.IOException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class RsocketClientApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(RsocketClientApplication.class, args);
		System.in.read();
	}

	@Bean
	RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
		return builder.tcp("localhost", 8888);
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> client(RSocketRequester client) {
		return  arg -> {
			Flux<GreetingResponse> greetingResponseFlux = client.route("greetings")
					.data(new GreetingRequest("Robert"))
					.retrieveFlux(GreetingResponse.class);

			greetingResponseFlux.subscribe(System.out::println);

			Mono<GreetingResponse> requestResponse = client.route("request-response")
					.data(new GreetingRequest("request-response :Robert"))
					.retrieveMono(GreetingResponse.class);

			requestResponse.subscribe(System.out::println);

			Mono<Void> fireAndForget = client.route("fire-and-forget")
					.data(new GreetingRequest("Robert"))
					.retrieveMono(Void.class);

			fireAndForget.subscribe(System.out::println);

			Flux<GreetingResponse> channel = client.route("stream-stream")
					.data(Flux.just(new GreetingRequest("Robert"),new GreetingRequest("Jerry"),new GreetingRequest("Rhys")))
					.retrieveFlux(GreetingResponse.class);

			channel.subscribe(System.out::println);
		};
	}

}
@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingResponse {
	private String message;
}
@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingRequest {
	private String name;
}
