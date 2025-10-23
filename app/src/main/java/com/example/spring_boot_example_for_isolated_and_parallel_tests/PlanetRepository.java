package com.example.spring_boot_example_for_isolated_and_parallel_tests;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanetRepository extends JpaRepository<Planet, Long> {
}
