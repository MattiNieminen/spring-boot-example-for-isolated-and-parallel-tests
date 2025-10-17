package com.example.spring_boot_example_for_isolated_and_parallel_tests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SpringBootExampleForIsolatedAndParallelTestsApplicationTests {

	@Test
	void contextLoads() {
	}

}
