package com.robert.rsocketguide;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class RsocketGuideApplicationTests {
	private static RSocketRequester requester;

	@BeforeAll
	public static void setupOnce(@Autowired RSocketRequester.Builder builder,
			@LocalRSocketServerPort Integer port) {
		requester = builder
				.tcp("localhost", port);
	}

	@Test
	public void testRequestGetsResponse() {
		Mono<GreetingResponse> response = requester
				.route("request-response")
				.data(new GreetingRequest("Robert"))
				.retrieveMono(GreetingResponse.class);

		StepVerifier
				.create(response)
				.consumeNextWith(message -> {
					assertThat(message.getMessage()).isEqualTo("HelloRobert");
				})
				.verifyComplete();
	}

	@Test
	public void testFireAndForget() {
		Mono<Void> result = requester
				.route("fire-and-forget")
				.data(new GreetingRequest("Robert"))
				.retrieveMono(Void.class);

		StepVerifier
				.create(result)
				.verifyComplete();
	}

	@Test
	public void testRequestStream() {
		Flux<GreetingResponse> stream = requester
				.route("greetings")
				.data(new GreetingRequest("Robert"))
				.retrieveFlux(GreetingResponse.class);

		StepVerifier
				.create(stream)
				.consumeNextWith(message -> {
					assertThat(message.getMessage()).contains("HelloRobert");
				})
				.expectNextCount(0)
				.consumeNextWith(message -> {
					assertThat(message.getMessage()).contains("HelloRobert");
				})
				.thenCancel()
				.verify();
	}

	@Test
	public void testStreamGetsStream() {
		Flux<GreetingRequest> just = Flux
				.just(new GreetingRequest("Robert"), new GreetingRequest("Jerry"),
						new GreetingRequest("Rhys"));

		Flux<GreetingResponse> stream = requester
				.route("stream-stream")
				.data(just)
				.retrieveFlux(GreetingResponse.class);

		StepVerifier
				.create(stream)
				.consumeNextWith(message -> {
					assertThat(message.getMessage()).isEqualTo("HelloRobert");
				})
				.consumeNextWith(message -> {
					assertThat(message.getMessage()).isEqualTo("HelloJerry");
				})
				.thenCancel()
				.verify();
	}
}
