package homerep.homerepoauth;

import homerep.homerepoauth.models.User;
import homerep.homerepoauth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

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
                    new User("habuma", encoder.encode("password"), "user@gmail.com", "71983942689","ROLE_ADMIN"));
            repo.save(
                    new User("tacochef", encoder.encode("password"), "user2@gmail.com", "81983942689","ROLE_ADMIN"));

        };
    }
    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
