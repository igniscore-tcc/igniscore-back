
package com.igniscore.api.exception;

import com.igniscore.api.service.subscription.SubscriptionAccessDeniedException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GraphQLExceptionHandler {

    @GraphQlExceptionHandler
    public GraphQLError handleSubscriptionAccessDenied(
            SubscriptionAccessDeniedException ex,
            DataFetchingEnvironment environment
    ) {
        return GraphqlErrorBuilder.newError(environment)
                .message(ex.getMessage())
                .build();
    }
}