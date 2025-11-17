package com.elearning.backend;

//import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BackendApplication {

	 static void main(String[] args) {

        SpringApplication.run(BackendApplication.class, args);
	}
//    @Bean
//    public CommandLineRunner createAdminUser(PasswordEncoder passwordEncoder) {
//        return args -> {
//            String plaintextPassword = "0423";
//            String hashedPassword = passwordEncoder.encode(plaintextPassword);
//
//            System.out.println("-----------------------------------------------------------------");
//            System.out.println("           FIRST ADMIN HASH (COPY THIS VALUE)                    ");
//            System.out.println("-----------------------------------------------------------------");
//            System.out.println("Plain Text: " + plaintextPassword);
//            System.out.println("HASHED VALUE: " + hashedPassword); // <-- COPY THIS STRING
//            System.out.println("-----------------------------------------------------------------");
//
//            // NOTE: Comment out this entire @Bean method after you have the hash!
//        };
//    }

}
