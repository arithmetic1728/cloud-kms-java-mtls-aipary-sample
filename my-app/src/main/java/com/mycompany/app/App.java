package com.mycompany.app;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.cloudkms.v1.CloudKMS;
import com.google.api.services.cloudkms.v1.CloudKMS.Projects.Locations.KeyRings;
import com.google.api.services.cloudkms.v1.model.ListKeyRingsResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import com.google.api.services.cloudkms.v1.CloudKMSScopes;

public class App {
  public static CloudKMS createClient(HttpTransport transport) throws GeneralSecurityException, IOException, Exception {
    GoogleCredentials credential = GoogleCredentials.getApplicationDefault()
        .createScoped(Collections.singleton(CloudKMSScopes.CLOUD_PLATFORM));

    // Create a HttpRequestInitializer, which will provide a baseline configuration to all requests.
    HttpRequestInitializer requestInitializer =
        request -> {
          new HttpCredentialsAdapter(credential).initialize(request);
          request.setConnectTimeout(60000); // 1 minute connect timeout
          request.setReadTimeout(60000); // 1 minute read timeout
        };

    // Build the client for interacting with the service.
    return new CloudKMS.Builder(transport, new JacksonFactory(), requestInitializer)
        .setApplicationName("my application").build();
  }

  public static void KeyringList(HttpTransport transport) throws Exception {
    // Initialize the client, which will be used to interact with the service.
    CloudKMS client = createClient(transport);

    // Results are paginated, so multiple queries may be required.
    String pageToken = null;

    do {
      // Create request and configure any parameters.
      KeyRings.List request =
          client.projects().locations().keyRings().list("projects/shinfan-mtls-demo/locations/global")
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
}
