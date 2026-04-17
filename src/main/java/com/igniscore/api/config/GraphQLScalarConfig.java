package com.igniscore.api.config;

import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * Configuration class responsible for registering custom GraphQL scalars.
 * This class extends the default GraphQL scalar types by adding support for
 * additional data types such as Date and DateTime using ExtendedScalars.
 */
@Configuration
public class GraphQLScalarConfig {

    /**
     * Configures the runtime wiring for GraphQL to include custom scalar types.
     * This method registers:
     * - Date scalar for handling date values
     * - DateTime scalar for handling date-time values
     *
     * @return a RuntimeWiringConfigurer that adds custom scalars to the GraphQL schema
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.Date)
                .scalar(ExtendedScalars.DateTime);
    }
}