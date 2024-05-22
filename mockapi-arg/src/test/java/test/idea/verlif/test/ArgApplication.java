package test.idea.verlif.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Verlif
 * @version 1.0
 */
@EnableCaching
@SpringBootApplication
public class ArgApplication extends SpringBootServletInitializer {

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(ArgApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }

    public static ConfigurableApplicationContext getContext() {
        return context;
    }

    public static void close() {
        context.close();
    }
}
