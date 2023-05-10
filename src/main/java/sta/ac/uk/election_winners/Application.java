package sta.ac.uk.election_winners;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws IOException{
        ConstituencyApiCaller apiCaller = new ConstituencyApiCaller();
        apiCaller.callApiAndSaveIdsToFile();

        SpringApplication.run(Application.class, args);
    }
}
