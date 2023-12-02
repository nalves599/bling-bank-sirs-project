package pt.ulisboa.tecnico.bling_bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class BlingBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlingBankApplication.class, args);
	}

}
