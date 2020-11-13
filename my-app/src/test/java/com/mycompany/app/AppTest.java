package com.mycompany.app;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.ProxySelector;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import com.google.api.client.googleapis.apache.v2.GoogleApacheHttpTransport;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.mtls.MtlsUtils;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.ssl.SSLContexts;
import org.junit.Test;

public class AppTest 
{
  // @Test
  // public void javaNetTest() throws Exception {
  // HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
  // App.KeyringList(transport);
  // }

  // @Test
  // public void apacheTest() throws Exception {
  // HttpTransport transport = GoogleApacheHttpTransport.newTrustedTransport();
  // App.KeyringList(transport);
  // }

  // @Test
  // public void apacheNewTest() throws Exception {
  // KeyStore ks = MtlsUtils.getDefaultMtlsProvider().getKeyStore();
  // SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(ks,
  // "".toCharArray()).build();

  // HttpClient httpClient =
  // HttpClients.custom().setSSLContext(sslContext).build();

  // HttpTransport transport = new ApacheHttpTransport(httpClient, true);
  // App.KeyringList(transport);
  // }

  @Test
  public void apacheDebugTest() throws Exception {
    KeyStore ks = MtlsUtils.getDefaultMtlsProvider().getKeyStore();
    SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(ks, "".toCharArray()).build();

    LayeredConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);

    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("https", socketFactory).build();

    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry,
        null, null, null, -1, TimeUnit.MILLISECONDS);

    connectionManager.setValidateAfterInactivity(-1);

    HttpClient httpClient = HttpClientBuilder.create().useSystemProperties().setSSLSocketFactory(socketFactory)
        .setMaxConnTotal(200).setMaxConnPerRoute(20)
        .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
        .setConnectionManager(connectionManager).disableRedirectHandling().disableAutomaticRetries().build();

    // HttpClient httpClient =
    // HttpClientBuilder.create().useSystemProperties().setSSLSocketFactory(socketFactory)
    // .setMaxConnTotal(200).setMaxConnPerRoute(20)
    // .setRoutePlanner(new
    // SystemDefaultRoutePlanner(ProxySelector.getDefault())).disableRedirectHandling()
    // .disableAutomaticRetries().build();

    HttpTransport transport = new ApacheHttpTransport(httpClient, true);
    App.KeyringList(transport);
  }
}
