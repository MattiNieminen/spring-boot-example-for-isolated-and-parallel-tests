package com.example.spring_boot_example_for_isolated_and_parallel_tests;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExampleTestB {

	@Autowired
	private PlanetRepository planetRepository;

	@Test
	void testB() {
		try { Thread.sleep(5000); } catch (InterruptedException e) {}
		Planet planet = new Planet("Venus", 12104);
		assertThat(planetRepository.findAll()).hasSize(0);
		planetRepository.save(planet);
		List<Planet> planets = planetRepository.findAll();
		assertThat(planets).hasSize(1);
		assertThat(planets.getFirst().getName()).isEqualTo(planet.getName());
	}
}
