package sta.ac.uk.election_winners;
import java.io.FileWriter;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConstituencyApiCaller {
    private static final String API_URL_TEMPLATE = "https://members-api.parliament.uk/api/Location/Constituency/%d";
    private static final int MAX_ID = 9999;
    private static final String FILENAME = "constituencyIds.csv";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

   public void callApiAndSaveIdsToFile() throws IOException, InterruptedException {
    FileWriter writer = new FileWriter(FILENAME);

    for (int id = 1; id <= MAX_ID; id++) {
        String apiUrl = String.format(API_URL_TEMPLATE, id);
        Request request = new Request.Builder().url(apiUrl).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.printf("Received response for ID %d: %s\n", id, responseBody);
                JsonNode jsonNode = mapper.readTree(responseBody);

                if (jsonNode.has("value")) {
                    JsonNode valueNode = jsonNode.get("value");

                    if (valueNode.has("currentRepresentation")) {
                        JsonNode currentRepresentationNode = valueNode.get("currentRepresentation");

                        if (currentRepresentationNode.has("member")) {
                            JsonNode memberNode = currentRepresentationNode.get("member");

                            if (memberNode.has("value") && memberNode.get("value").has("nameListAs")) {
                                String memberName = memberNode.get("value").get("nameListAs").asText();
                                System.out.printf("ID %d: Member name found: %s\n", id, memberName);
                                String gender = memberNode.get("value").get("gender").asText();
                                String membershipStartDate = memberNode.get("value").get("latestHouseMembership").get("membershipStartDate").asText();
                                writer.write(String.format("%d,%s,%s,%s\n", id, memberName, gender, membershipStartDate));
                                writer.flush();  // flush the writer to ensure the data is written to disk
                                Thread.sleep(500);  // wait for 0.5 seconds
                            } else {
                                System.out.printf("ID %d: Member name not found\n", id);
                            }
                        } else {
                            System.out.printf("ID %d: Member object not found\n", id);
                        }
                    } else {
                        System.out.printf("ID %d: Current representation object not found\n", id);
                    }
                } else {
                    System.out.printf("ID %d: Value object not found\n", id);
                }
            } else {
                System.out.printf("Request for ID %d failed: %d %s\n", id, response.code(), response.message());
            }
        }
    }

    writer.close();
}
}
