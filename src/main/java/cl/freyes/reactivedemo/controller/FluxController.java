package cl.freyes.reactivedemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@Slf4j
public class FluxController {


    @GetMapping(path = "/test_flux", produces = "text/event-stream")
    public Flux<Integer> all() {
        Flux<Integer> flux = Flux.range(1, 30)
                .delayElements(Duration.ofSeconds(1)).map(i->i*i);

        flux.subscribe(l->log.info("mult {}",l));
        return flux; // retornamos el elemento. Sería como el suscriptor 3
    }
}