package com.example.spring_boot_example_for_isolated_and_parallel_tests;

import org.springframework.boot.SpringApplication;

public class TestSpringBootExampleForIsolatedAndParallelTestsApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringBootExampleForIsolatedAndParallelTestsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
