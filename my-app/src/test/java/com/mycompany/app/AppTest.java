package com.mycompany.app;

import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.apache.v2.GoogleApacheHttpTransport;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.mtls.MtlsUtils;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.util.SslUtils;

import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
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
