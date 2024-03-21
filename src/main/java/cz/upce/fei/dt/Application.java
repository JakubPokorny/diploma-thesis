package cz.upce.fei.dt;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

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

    //@Bean
//    public CommandLineRunner commandLineRunner(
//            ContactRepository contactRepository
//    )
//    {
//        return args -> {
//            ExampleDataGenerator<Contact> generator = new ExampleDataGenerator<>(Contact.class, LocalDateTime.now());
//            generator.setData(Contact::setName, DataType.COMPANY_NAME);
//            //generator.setData(Contact::setICO, DataType.NUMBER_UP_TO_10000.toString());
//            //generator.setData(Contact::setInvoiceAddress, DataType<Address>);
//            List<Contact> list = generator.create(100, 1);
//        };
//    }
}
