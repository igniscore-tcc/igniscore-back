import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import graphql.schema.GraphQLScalarType;

@Configuration
public class GraphQLScalarConfig {

    @Bean
    public GraphQLScalarType dateScalar() {
        return ExtendedScalars.Date;
    }
}