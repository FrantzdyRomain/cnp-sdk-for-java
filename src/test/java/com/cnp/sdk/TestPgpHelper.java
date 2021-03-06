package com.cnp.sdk;

import org.bouncycastle.openpgp.PGPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestPgpHelper {
    private String direct;
    private String requestFilename;
    private String encryptedRequestFilename;
    private String publicKeyPath;
    private String privateKeyPath;
    private String responseFilename;
    private String decryptedResponseFilename;
    private String passphrase;

    @Before
    public void setup() throws Exception {
        String workingDir = System.getProperty("java.io.tmpdir");
        direct = workingDir + File.separator + "test";
        File fRequestDir = new File(direct);
        fRequestDir.mkdirs();

        requestFilename = direct + File.separator + "test.txt";
        String input = "This is the text to be encrypted. Java SDK V12";
        BufferedWriter out = new BufferedWriter(new FileWriter(requestFilename));
        out.write(input);
        encryptedRequestFilename = direct + File.separator + "test.asc";
        responseFilename = direct + File.separator + "test.asc";
        decryptedResponseFilename = direct + File.separator + "test2.txt";
        Properties properties = new Properties();
        properties.load(new FileInputStream(new Configuration().location()));
        publicKeyPath = properties.getProperty("PublicKeyPath");
        privateKeyPath = properties.getProperty("PrivateKeyPath");
        passphrase = properties.getProperty("gpgPassphrase");
    }

    @Test
    public void testEncryptDecrypt() {

        try {
            PgpHelper.encrypt(requestFilename, encryptedRequestFilename, publicKeyPath);
            assertTrue(new File(encryptedRequestFilename).exists());
            PgpHelper.decrypt(responseFilename, decryptedResponseFilename, privateKeyPath,passphrase);
            assertTrue(new File(decryptedResponseFilename).exists());

            String input = new String(Files.readAllBytes(Paths.get(requestFilename)));
            String output = new String(Files.readAllBytes(Paths.get(decryptedResponseFilename)));
            assertEquals(input, output);
        } catch (IOException | PGPException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        File fRequestDir = new File(direct);
        if(fRequestDir.exists()){
            for (String file : fRequestDir.list()) {
                File fileHandle = new File(fRequestDir.getPath(),file);
                fileHandle.delete();
            }
        }
        fRequestDir.delete();
    }
}
