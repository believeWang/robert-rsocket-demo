package com.robert.rsocketguide;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class RsocketGuideApplication {

  public static void main(String[] args) {
    SpringApplication.run(RsocketGuideApplication.class, args);
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

@Controller
@Slf4j
class GreetingController {

  @MessageMapping("greetings")
  Flux<GreetingResponse> greet(GreetingRequest request) {
    log.info("greetings name:{}", request.getName());
    Stream<GreetingResponse> generate = Stream.generate(
        () -> new GreetingResponse("Hello" + request.getName() + "@" + Instant.now()));

    return Flux.fromStream(generate)
        .delayElements(Duration.ofSeconds(1)).take(10);

  }

  @MessageMapping("request-response")
  Mono<GreetingResponse> requestResponse(GreetingRequest request) {
    log.info("request-response name:{}", request.getName());
    return Mono.just(new GreetingResponse("Hello" + request.getName()));

  }
  @MessageMapping("fire-and-forget")
  Mono<Void> fireAndForget(GreetingRequest request) {
    log.info("fire-and-forget name:{}", request.getName());
    return Mono.empty();

  }
  @MessageMapping("stream-stream")
  Flux<GreetingResponse> channel(Flux<GreetingRequest> request) {
    log.info("channel....");
    return request.
        map(rq -> new GreetingResponse("Hello" +rq.getName()));

  }

}

