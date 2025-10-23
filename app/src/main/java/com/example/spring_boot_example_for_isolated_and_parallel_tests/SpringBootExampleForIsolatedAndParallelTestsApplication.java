package com.example.spring_boot_example_for_isolated_and_parallel_tests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class SpringBootExampleForIsolatedAndParallelTestsApplication {

    private final PlanetRepository planetRepository;

    public SpringBootExampleForIsolatedAndParallelTestsApplication(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

	public static void main(String[] args) {
		SpringApplication.run(SpringBootExampleForIsolatedAndParallelTestsApplication.class, args);
	}

    @EventListener(ApplicationReadyEvent.class)
    public void seedPlanets() {
        if (planetRepository.count() == 0) {
            planetRepository.save(new Planet("Mercury", 4879));
            planetRepository.save(new Planet("Earth", 12742));
            planetRepository.save(new Planet("Saturn", 116460));
        }
    }

}
