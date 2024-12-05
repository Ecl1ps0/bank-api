package com.bank_api.bank.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

@Configuration
@EnableMongoRepositories
public class ReactiveMongoConfig extends AbstractReactiveMongoConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    @Bean
    public MongoClient reactiveMongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .retryWrites(true) // Retry writes in case of transient network issues
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder -> {
                    builder.maxSize(50) // Maximum connections in the pool
                            .minSize(5)   // Minimum connections in the pool
                            .maxConnectionLifeTime(30, TimeUnit.MINUTES) // Maximum life of a connection
                            .maxConnectionIdleTime(10, TimeUnit.MINUTES); // Maximum idle time
                })
                .applyToSocketSettings(builder -> {
                    builder.connectTimeout(5000, TimeUnit.MILLISECONDS); // Connection timeout
                })
                .applyToSslSettings(builder -> {
                    builder.enabled(true);
                    builder.invalidHostNameAllowed(false);
                })
                .applicationName("bank-api") // Application name for MongoDB logs
                .build();

        return MongoClients.create(clientSettings);
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(MongoClient mongoClient) {
        return new ReactiveMongoTemplate(mongoClient, getDatabaseName());
    }

    @Override
    protected String getDatabaseName() {
        return "bank";
    }
}
