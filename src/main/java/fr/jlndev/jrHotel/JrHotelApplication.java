package fr.jlndev.jrHotel;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import fr.jlndev.jrHotel.entity.User;
import fr.jlndev.jrHotel.repo.UserRepository;

@SpringBootApplication
public class JrHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(JrHotelApplication.class, args);
    }

    /*@Bean
    CommandLineRunner passwordCheck(PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("PASSWORD OK = "
                    + passwordEncoder.matches(
                            "password",
                            "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
                    ));
        };
    }

    @Bean
CommandLineRunner checkPassword(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    return args -> {
        User admin = userRepository.findByEmail("admin@jr-hotel.fr").orElseThrow();

        System.out.println("EMAIL DB = " + admin.getEmail());
        System.out.println("PASSWORD DB = " + admin.getPassword());
        System.out.println("PASSWORD OK = " + passwordEncoder.matches("password", admin.getPassword()));
    };
    
}*/

/*@Bean
CommandLineRunner resetPasswords(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    return args -> {
        User admin = userRepository.findByEmail("admin@jr-hotel.fr").orElseThrow();
        admin.setPassword(passwordEncoder.encode("password"));
        userRepository.save(admin);

        User user = userRepository.findByEmail("user@jr-hotel.fr").orElseThrow();
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);

        System.out.println("ADMIN PASSWORD OK = " + passwordEncoder.matches("password", admin.getPassword()));
        System.out.println("USER PASSWORD OK = " + passwordEncoder.matches("password", user.getPassword()));
        System.out.println("*********** ADMIN PASSWORD **********" );
        System.out.println( passwordEncoder.encode("password"));
    };
}*/

}
