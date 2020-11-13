package com.mycompany.app;

import static org.junit.Assert.assertTrue;

import com.google.api.client.googleapis.apache.v2.GoogleApacheHttpTransport;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

public class AppTest 
{
  // @Test
  // public void javaNetTest() throws Exception {
  // HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
  // App.KeyringList(transport);
  // }

  // @Test
  // public void apacheTest() throws Exception
  // {
  // HttpTransport transport = GoogleApacheHttpTransport.newTrustedTransport();
  // App.KeyringList(transport);
  // }

  @Test
  public void apacheNewTest() throws Exception {
    HttpClient client = HttpClientBuilder.create().useSystemProperties()
        .setSSLSocketFactory(SSLConnectionSocketFactory.getSystemSocketFactory()).build();
    HttpTransport transport = new ApacheHttpTransport(client, true);
    App.KeyringList(transport);
  }
}
