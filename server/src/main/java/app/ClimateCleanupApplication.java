package app;

import app.repositories.team.TeamRepository;
import app.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.transaction.Transactional;
import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class ClimateCleanupApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ClimateCleanupApplication.class, args);
	}
}
