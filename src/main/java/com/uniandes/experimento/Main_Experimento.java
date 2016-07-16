package com.uniandes.experimento;



import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import io.dropwizard.setup.Bootstrap;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.skife.jdbi.v2.DBI;
import com.uniandes.experimento.resources.EpisodeResource;
import com.uniandes.experimento.resources.UserResource;

import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;


public class Main_Experimento extends Application<LoginConfiguration> {

    public static void main(String[] args) throws Exception {
        new Main_Experimento().run(args);
    }

    @Override
    public String getName() {
        return "sistemaColegioColpedagogico";
    }

    @Override
    public void initialize(Bootstrap<LoginConfiguration> bootstrap) {
    }

    @Override
    public void run(LoginConfiguration configuration, Environment environment) throws ClassNotFoundException {
        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");

        final UserResource userResource = new UserResource(jdbi);
        final EpisodeResource episodeResource = new EpisodeResource(jdbi);

        //Registramos el primero recurso
        environment.jersey().register(userResource);
        //Registramos el segundo recurso
        environment.jersey().register(episodeResource);
    }
}
