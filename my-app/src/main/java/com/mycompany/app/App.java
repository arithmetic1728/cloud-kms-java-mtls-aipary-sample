package com.mycompany.app;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.cloudkms.v1.CloudKMS;
import com.google.api.services.cloudkms.v1.CloudKMS.Projects.Locations.KeyRings;
import com.google.api.services.cloudkms.v1.model.ListKeyRingsResponse;
import com.google.api.services.cloudkms.v1.CloudKMSScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Collections;

public class App {
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  private static CloudKMS createClient() throws IOException {
    // Use Application Default Credentials (ADC) to authenticate the requests
    // For more information see https://cloud.google.com/docs/authentication/production
    GoogleCredentials credential =
        GoogleCredentials.getApplicationDefault().createScoped(Collections.singleton(CloudKMSScopes.CLOUD_PLATFORM));

    // Create a HttpRequestInitializer, which will provide a baseline configuration to all requests.
    HttpRequestInitializer requestInitializer =
        request -> {
          new HttpCredentialsAdapter(credential).initialize(request);
          request.setConnectTimeout(60000); // 1 minute connect timeout
          request.setReadTimeout(60000); // 1 minute read timeout
        };

    // Build the client for interacting with the service.
    return new CloudKMS.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
        .setApplicationName("your-application-name")
        .build();
  }

  public static void KeyringList(String projectId, String regionId) throws IOException {
    // Initialize the client, which will be used to interact with the service.
    CloudKMS client = createClient();

    // Results are paginated, so multiple queries may be required.
    String pageToken = null;

    do {
      // Create request and configure any parameters.
      KeyRings.List request =
          client.projects().locations().keyRings().list("projects/study-auth-265119/locations/global")
              .setPageSize(100) // Specify pageSize up to 1000
              .setPageToken(pageToken);

      // Execute response and collect results.
      ListKeyRingsResponse response = request.execute();
      System.out.println(response.toString());

      // Update the page token for the next request.
      pageToken = response.getNextPageToken();
      System.out.println(pageToken);
    } while (pageToken != null);

  }

  public static void main(String[] args) throws IOException {
    App.KeyringList("study-auth-265119", "us-central1");
    System.out.println("This will be printed");
  }
}
