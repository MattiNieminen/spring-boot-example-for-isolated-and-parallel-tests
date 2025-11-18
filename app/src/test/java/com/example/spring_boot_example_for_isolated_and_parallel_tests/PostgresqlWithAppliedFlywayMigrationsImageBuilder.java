package com.example.spring_boot_example_for_isolated_and_parallel_tests;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.NotFoundException;

public class PostgresqlWithAppliedFlywayMigrationsImageBuilder {

    private static final String TARGET_REPOSITORY = "postgresql-with-applied-flyway-migrations";
    private static final String TARGET_TAG = "latest";
    private static final String TARGET_IMAGE = TARGET_REPOSITORY + ":" + TARGET_TAG;
    private static final String POSTGRES_IMAGE = "postgres:latest";
    private static final String FLYWAY_IMAGE = "flyway/flyway:latest";

    private Logger log = LoggerFactory.getLogger(PostgresqlWithAppliedFlywayMigrationsImageBuilder.class);

    private final DockerClient dockerClient;

    public PostgresqlWithAppliedFlywayMigrationsImageBuilder() {
        // Testcontainers requires update with newer Docker
        // https://github.com/testcontainers/testcontainers-java/issues/11212
        System.setProperty("api.version", "1.44");

        this.dockerClient = DockerClientFactory.lazyClient();
    }

    // TODO implement rebuilding image only if files have changed
    public void ensureImageBuilt() {
        if (imageExists()) {
            log.info("Database migrations have changed. Rebuilding the Docker image.");
            deleteImage();
        }

        buildImage();
    }

    private boolean imageExists() {
        try {
            dockerClient.inspectImageCmd(TARGET_IMAGE).exec();
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    private void buildImage() {
        log.info("Building new image.");

        try (Network network = Network.newNetwork();
                GenericContainer<?> postgresqlContainer = new GenericContainer<>(DockerImageName.parse(POSTGRES_IMAGE));
                GenericContainer<?> flywayContainer = new GenericContainer<>(DockerImageName.parse(FLYWAY_IMAGE))) {

            postgresqlContainer.withNetwork(network);
            postgresqlContainer.withNetworkAliases("postgresql");
            postgresqlContainer.addEnv("POSTGRES_PASSWORD", "example");

            // Because by default the Postgresql data directory inside container depends on
            // version and is mounted as volume, let's move it.
            postgresqlContainer.addEnv("PGDATA", "/pgdata");
            postgresqlContainer.waitingFor(Wait.forSuccessfulCommand("pg_isready -U postgres"));

            String migrationsPathOnHost = Paths.get("../db-migrations").toAbsolutePath().toString();

            flywayContainer.withNetwork(network);
            flywayContainer.withCopyFileToContainer(MountableFile.forHostPath(migrationsPathOnHost), "/flyway/sql");
            flywayContainer.withEnv("FLYWAY_URL", "jdbc:postgresql://postgresql:5432/postgres");
            flywayContainer.withEnv("FLYWAY_USER", "postgres");
            flywayContainer.withEnv("FLYWAY_PASSWORD", "example");
            flywayContainer.withCommand("migrate");

            flywayContainer.waitingFor(Wait.forLogMessage(".*Successfully applied .* migration.*", 1));

            postgresqlContainer.start();
            flywayContainer.start();

            // Labels that prevent Testcontainers to clean the created image.
            Map<String, String> labels = new HashMap<>();
            labels.put("org.testcontainers", "true");
            labels.put("org.testcontainers.lang", "java");
            labels.put("org.testcontainers.sessionId", "manual-build");
            labels.put("org.testcontainers.desktop.keep", "true");
            labels.put("tc.keep", "true");

            dockerClient.commitCmd(postgresqlContainer.getContainerId())
                    .withRepository(TARGET_REPOSITORY)
                    .withTag(TARGET_TAG)
                    .withLabels(labels)
                    .exec();

            log.info("New Postgresql image created with database migrations applied.");

        }
    }

    public void deleteImage() {
        log.info("Deleting existing image.");
        dockerClient.removeImageCmd(TARGET_IMAGE).withForce(true).exec();
    }

}
