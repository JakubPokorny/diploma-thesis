package cz.upce.fei.dt;

import com.github.javafaker.Faker;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.ProductComponent;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.entities.keys.ProductComponentKey;
import cz.upce.fei.dt.beckend.repositories.ComponentRepository;
import cz.upce.fei.dt.beckend.repositories.ProductComponentRepository;
import cz.upce.fei.dt.beckend.repositories.ProductRepository;
import cz.upce.fei.dt.beckend.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The entry point of the Spring Boot application.
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
//@NpmPackage(value = "lumo-css-framework", version = "^4.0.10")
@Theme(value = "is-diploma-thesis")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            ComponentRepository componentRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            ProductComponentRepository productComponentRepository
    )
    {

        return args -> {
//            Faker faker = new Faker();
//
//            List<User> users = userRepository.findAll();
//            List<Product> products = productRepository.findAll();
//            final int MAX_IN_STOCK = 100;
//            final int MAX_MIN_IN_STOCK = 10;
//            final int MAX_AMOUNT = 5;
//            for (int i = 0; i < 100; i++) {
//                Component component = Component.builder()
//                        .name(faker.commerce().material())
//                        .description(faker.gameOfThrones().quote())
//                        .inStock(faker.random().nextInt(0, MAX_IN_STOCK))
//                        .minInStock(faker.random().nextInt(0, MAX_MIN_IN_STOCK))
//                        .user(users.get(faker.random().nextInt(0, users.size()-1)))
//                        .productComponents(Collections.emptySet())
//                        .build();
//
//
//                component = componentRepository.save(component);
//
//                for (int j = 0; j < faker.random().nextInt(0, 5); j++) {
//                    Product product = products.get(faker.random().nextInt(0, products.size()-1));
//
//                    ProductComponent productComponent = ProductComponent
//                            .builder()
//                            .id(new ProductComponentKey(product.getId(), component.getId()))
//                            .amount(faker.random().nextInt(1, MAX_AMOUNT))
//                            .product(product)
//                            .component(component)
//                            .build();
//
//                    productComponentRepository.save(productComponent);
//                }
//
//            }
        };
    }
}
