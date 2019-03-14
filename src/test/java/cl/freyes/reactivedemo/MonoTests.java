package cl.freyes.reactivedemo;

import cl.freyes.reactivedemo.dto.Person;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
public class MonoTests {

    @Test
    public void mono_do_nothing() {
        Mono<Person> personMono = Mono.just(new Person("Francisco")).log();
        Mono<String> personFiltered = personMono.map(s -> s.getName().toUpperCase());

        personMono.subscribe(s -> log.info("{}", s));
        personFiltered.subscribe(s -> log.info("filtered {}", s));


    }

    @Test
    public void mono_one_observer() {
        Mono<Person> personMono = Mono.just(new Person("Francisco"));
        personMono.subscribe(p -> log.info(p.getName()));
    }

    @Test
    public void mono_two_observers() {
        Mono<Person> personMono = Mono.just(new Person("Francisco"));
        personMono.subscribe(p -> log.info(p.getName()));
        personMono.subscribe(p -> log.info("The name is {} ", p.getName()));


    }

    @Test
    public void mono_subscribe_duration() {
        Mono<Person> personMono = Mono.just(new Person("Francisco")).delayElement(Duration.ofSeconds(2));
        Mono<Person> personMono2 = Mono.just(new Person("Pancho")).delayElement(Duration.ofSeconds(5));
        personMono2.subscribe(p -> log.info("The name is {} ", p.getName()));
        personMono.subscribe(p -> log.info("The name is {} ", p.getName()));
    }

    @Test
    public void mono_subscribe_delay_subscription() {
        Mono<Person> personMono = Mono.just(new Person("Francisco")).delaySubscription(Duration.ofSeconds(2));
        Mono<Person> personMono2 = Mono.just(new Person("Pancho")).delaySubscription(Duration.ofSeconds(5));
        personMono2.subscribe(p -> log.info("The name is {} ", p.getName()));
        personMono.subscribe(p -> log.info("The name is {} ", p.getName()));
    }

    @Test
    public void mono_no_blocking() {
        Mono<Person> personMono = Mono.just(new Person("Francisco")).delayElement(Duration.ofSeconds(1)).subscribeOn(Schedulers.parallel());
        Mono<Person> personMono2 = Mono.just(new Person("Pancho")).delayElement(Duration.ofSeconds(3)).subscribeOn(Schedulers.elastic());

        personMono.subscribe(p -> log.info("The name is {} ", p.getName()));
        personMono2.subscribe(p -> log.info("The name is {} ", p.getName()));

        try {
            Thread.sleep(4000);
            log.info("Main thread's finished");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void mono_blocking() {
        Mono<Person> personMono = Mono.just(new Person("Francisco")).delayElement(Duration.ofSeconds(1)).subscribeOn(Schedulers.parallel());
        Mono<Person> personMono2 = Mono.just(new Person("Pancho")).delayElement(Duration.ofSeconds(3)).subscribeOn(Schedulers.elastic());

        log.info("The name is {} ", personMono.block().getName());
        log.info("The name is {} ", personMono2.block().getName());

        try {
            Thread.sleep(4000);
            log.info("Main thread's finished");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
