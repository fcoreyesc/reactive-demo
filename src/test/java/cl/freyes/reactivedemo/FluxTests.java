package cl.freyes.reactivedemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class FluxTests {

    @Test
    public void flux_one_observer() {
        Flux<Integer> flux = Flux.range(1, 500);
        flux.subscribe(System.out::println);
        log.info("It's finished");
    }

    @Test
    public void flux_two_observers() {
        Flux<Integer> flux = Flux.range(1, 10);
        flux.subscribe(i -> {
            try {
                Thread.sleep(50l);
                log.info("mult = {}", i * i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        flux.subscribe(System.out::println);

        log.info("It's finished");

    }

    @Test
    public void flux_two_observers_elastic() {
        Flux<Integer> flux = Flux.range(1, 3000).subscribeOn(Schedulers.elastic());
        flux.subscribe(i ->
                log.info("multiply = {}", i * i));
        flux.subscribe(i -> log.info("just number {}", i));


        //log.info("It's finished {}", flux.blockLast());

    }

    @Test
    public void flux_two_observers_daemon_thread() {
        Flux<Integer> flux = Flux.range(1, 300).subscribeOn(Schedulers.newSingle("New thread -1 ", true));
        flux.subscribe(i ->
                log.info("multiply = {}", i * i));
        flux.subscribe(i -> log.info("just number {}", i));


        log.info("It's finished {}", 1);

    }


    @Test
    public void flux_two_observers_no_daemon_thread() {
        Flux<Integer> flux = Flux.range(1, 300).subscribeOn(Schedulers.newSingle("New thread -1 ", false));
        flux.subscribe(i ->
                log.info("multiply = {}", i * i));
        flux.subscribe(i -> log.info("just number {}", i));


        log.info("It's finished {}", flux.blockLast());

    }

    @Test
    public void flux_to_mono() {
        Flux.just(1, 2).reduce(0, (a, b) -> a + b).subscribe(System.out::println);
        log.info("It's finished");
    }


    @Test
    public void combining_streams() {
        List<String> elements = new ArrayList<>();
        Flux.just(1, 2, 3, 4)
                .log()
                .map(i -> i * 2)
                .zipWith(Flux.range(0, Integer.MAX_VALUE),
                        (two, one) -> String.format("First Flux: %d, Second Flux: %d", one, two))
                .subscribe(elements::add);

    }

    @Test
    public void hot_streams() {
        ConnectableFlux<Object> publish = Flux.create(fluxSink -> {
            while (true) {
                fluxSink.next(System.currentTimeMillis());
            }
        }).publish();

        publish.subscribe(System.out::println);
        publish.subscribe(System.out::println);

//        publish.connect();

    }

    @Test
    public void hot_streams_throttling() {
        ConnectableFlux<Object> publish = Flux.create(fluxSink -> {

            while (true) {
                fluxSink.next(System.currentTimeMillis() /1000/60);
            }
        }).sample(Duration.ofSeconds(2)).log().publish();

        publish.subscribe(System.out::println);
        publish.subscribe(System.out::println);

        publish.connect();

    }


}
