# robert-rsocket-demo
demo spring rsocket

## rsocket-guide
Server的部分

## rsocket-client
Client的部分

#### rsc
[RSocket Client CLI (RSC)](https://github.com/making/rsc) 是由一名日本人Toshiaki Maki開發，是專門用來呼叫RSocket的command line服務，等同於之前使用的curl。
實際呼叫上面寫的服務結果如下，感覺debug模式十分強大，非常詳細。

windos : 
```
rsc tcp://localhost:8888 --stream --route greetings --log --debug -d "{\""name\"":\""Robert\""}"

rsc tcp://localhost:8888 --route request-response --log --debug -d "{\""name\"":\""Robert\""}"

rsc tcp://localhost:8888  --route fire-and-forget --log --debug -d "{\""name\"":\""Robert\""}"

rsc tcp://localhost:8888 --channel --route stream-stream --log --debug -d -

{"name":"robert"} 
{"name":"jerry"}
```



linux-base : `rsc tcp://localhost:8888 --stream --route greetings --log --debug -d "{\"name\":\"Josh\"}"`


#### Spring Retrosocket
在微服務當中，少不了穿插許多呼叫其他服務的部分，若只用RestTemplate程式碼相對會比較雜亂，[Spring Cloud OpenFeign](https://cloud.spring.io/spring-cloud-openfeign/reference/html/) 提供了相對乾淨整齊的服務來解決這個問題，而[Spring Retrosocket](https://spring-projects-experimental.github.io/spring-retrosocket/)就是提供給RSocket Feign風格的client端，目前還在開發實驗階段。
