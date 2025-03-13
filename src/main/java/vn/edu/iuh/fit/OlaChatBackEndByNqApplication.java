package vn.edu.iuh.fit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
@SpringBootApplication
public class OlaChatBackEndByNqApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("REDIS_HOST", dotenv.get("REDIS_HOST"));
        System.setProperty("REDIS_PORT", dotenv.get("REDIS_PORT"));
        System.setProperty("REDIS_USER", dotenv.get("REDIS_USER"));
        System.setProperty("REDIS_PASS", dotenv.get("REDIS_PASS"));
        System.setProperty("ACCOUNT_SID", dotenv.get("ACCOUNT_SID"));
        System.setProperty("AUTH_TOKEN", dotenv.get("AUTH_TOKEN"));
        System.setProperty("TWILIO_PHONE_NUMBER", dotenv.get("TWILIO_PHONE_NUMBER"));

        SpringApplication.run(OlaChatBackEndByNqApplication.class, args);
    }

}
