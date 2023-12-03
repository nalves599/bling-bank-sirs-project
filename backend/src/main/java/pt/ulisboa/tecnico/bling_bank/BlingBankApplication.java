package pt.ulisboa.tecnico.bling_bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
@EnableJpaAuditing
@PropertySource("classpath:application.properties")
public class BlingBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlingBankApplication.class, args);
    }

}
