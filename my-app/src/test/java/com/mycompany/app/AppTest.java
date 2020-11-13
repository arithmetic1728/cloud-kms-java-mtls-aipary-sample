package com.mycompany.app;

import com.google.api.client.googleapis.apache.v2.GoogleApacheHttpTransport;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import org.junit.Test;

public class AppTest 
{
  @Test
  public void javaNetTest() throws Exception {
    HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
    App.KeyringList(transport);
  }

  @Test
  public void apacheTest() throws Exception {
    HttpTransport transport = GoogleApacheHttpTransport.newTrustedTransport();
    App.KeyringList(transport);
  }
}
