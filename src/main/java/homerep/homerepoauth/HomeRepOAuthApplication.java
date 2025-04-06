package homerep.homerepoauth;

import homerep.homerepoauth.models.User;
import homerep.homerepoauth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class HomeRepOAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeRepOAuthApplication.class, args);
    }

    @Bean
    public ApplicationRunner dataLoader(
            UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            repo.save(
                    new User("habuma", encoder.encode("password"), "user@gmail.com", "+71983942689","ADMIN"));
            repo.save(
                    new User("tacochef", encoder.encode("password"), "user2@gmail.com", "+81983942689","ADMIN"));

        };
    }
}
