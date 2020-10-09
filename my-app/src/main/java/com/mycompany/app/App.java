package com.mycompany.app;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.cloudkms.v1.CloudKMS;
import com.google.api.services.cloudkms.v1.CloudKMS.Projects.Locations.KeyRings;
import com.google.api.services.cloudkms.v1.model.ListKeyRingsResponse;
import com.google.api.services.cloudkms.v1.CloudKMSScopes;
import com.google.api.client.googleapis.GoogleUtils;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Collections;

public class App {
  private static KeyStore getKeystore() throws Exception {
    // /opt/google/endpoint-verification/bin/apihelper --print_certificate
    // /opt/google/endpoint-verification/bin/apihelper --print_certificate --with_passphrase
    
    // String pem_path = "unencrypted.pem";
    // String passphrase = "";

    String pem_path = "encrypted.pem";
    String passphrase = "";

    Security.addProvider(new BouncyCastleProvider());
    String delimiter = "-----END CERTIFICATE-----"; 
    File file = new File("/usr/local/google/home/sijunliu/wks/java_client/cloud-kms-java-mtls-aipary-sample/my-app/" + pem_path);
    byte[] certAndKey = Files.readAllBytes(file.toPath());
    String[] tokens = new String(certAndKey).split(delimiter); 
    byte[] certBytes = tokens[0].concat(delimiter).getBytes(); 
    byte[] keyBytes = tokens[1].getBytes(); 
 
    CertificateFactory fact = CertificateFactory.getInstance("X.509");
    X509Certificate cert = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(certBytes)); 

    KeyStore keystore = KeyStore.getInstance("JKS"); 
    keystore.load(null); 
    keystore.setCertificateEntry("cert-alias", cert); 

    PEMParser pem = new PEMParser(new StringReader(new String(keyBytes)));
    Object pk = pem.readObject();
    System.out.printf("pem type is %s", pk.getClass().getName());

    if (pk instanceof PrivateKeyInfo) {
      PrivateKey key = new JcaPEMKeyConverter().getPrivateKey((PrivateKeyInfo) pk);
      keystore.setKeyEntry("alias", key, new char[]{}, new X509Certificate[] {cert});
    } else {
      // decrypt and convert key
      InputDecryptorProvider decryptionProv = new JceOpenSSLPKCS8DecryptorProviderBuilder().build(passphrase.toCharArray());
      PrivateKeyInfo keyInfo = ((PKCS8EncryptedPrivateKeyInfo)pk).decryptPrivateKeyInfo(decryptionProv);
      PrivateKey key = new JcaPEMKeyConverter().getPrivateKey(keyInfo);
      keystore.setKeyEntry("alias", key, new char[]{}, new X509Certificate[] {cert});
    }

    return keystore;
  }

  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  private static CloudKMS createClient() throws Exception {
    NetHttpTransport HTTP_TRANSPORT = 
    new NetHttpTransport.Builder().trustCertificates(GoogleUtils.getCertificateTrustStore(), getKeystore())
        .build();
    // Use Application Default Credentials (ADC) to authenticate the requests
    // For more information see https://cloud.google.com/docs/authentication/production
    SimpleDateFormat dateformat3 = new SimpleDateFormat("dd/MM/yyyy");
    String token_str = "";
    AccessToken token = new AccessToken(token_str, dateformat3.parse("27/09/2032"));
    UserCredentials creds = UserCredentials.newBuilder()
    .setClientId("clientId")
    .setClientSecret("clientSecret")
    .setRefreshToken("refreshToken")
    .setAccessToken(token)
    .build();
    
    // GoogleCredentials credential =
    //     GoogleCredentials.getApplicationDefault().createScoped(Collections.singleton(CloudKMSScopes.CLOUD_PLATFORM));

    // Create a HttpRequestInitializer, which will provide a baseline configuration to all requests.
    HttpRequestInitializer requestInitializer =
        request -> {
          new HttpCredentialsAdapter(creds).initialize(request);
          request.setConnectTimeout(60000); // 1 minute connect timeout
          request.setReadTimeout(60000); // 1 minute read timeout
        };

    // Build the client for interacting with the service.
    return new CloudKMS.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
        .setRootUrl("https://cloudkms.mtls.googleapis.com/")
        .setApplicationName("your-application-name")
        .build();
  }

  public static void KeyringList(String projectId, String regionId) throws Exception {
    // Initialize the client, which will be used to interact with the service.
    CloudKMS client = createClient();

    // Results are paginated, so multiple queries may be required.
    String pageToken = null;

    do {
      // Create request and configure any parameters.
      KeyRings.List request =
          client.projects().locations().keyRings().list("projects/dcatest-281318/locations/global")
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

  public static void main(String[] args) throws Exception {
    String project = "dcatest-281318";
    App.KeyringList(project, "us-central1");
    System.out.println("This will be printed");
  }
}
