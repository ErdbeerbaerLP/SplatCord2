package de.erdbeerbaerlp.splatcord2.util.wiiu;

import com.arbiter34.byml.BymlFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Byml;
import de.erdbeerbaerlp.splatcord2.util.SSLBypass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.util.Arrays;

/**
 * Most code translated from https://github.com/PretendoNetwork/boss-js/tree/master/lib
 */
public class BossFileUtil {

    private static byte[] hexStringToBytes(String hexString) {
        int length = hexString.length();
        byte[] output = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            output[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return output;
    }


    // == Code translated from Pretendo ==

    private static final int BOSS_MAGIC = ByteBuffer.wrap("boss".getBytes(StandardCharsets.UTF_8)).getInt();
    private static final int BOSS_CTR_VER = 0x10001;
    private static final int BOSS_WUP_VER = 0x20001;

    private static final byte[] BOSS_AES_KEY_HASH = hexStringToBytes("5202ce5099232c3d365e28379790a919");
    private static final byte[] BOSS_HMAC_KEY_HASH = hexStringToBytes("b4482fef177b0100090ce0dbeb8ce977");


    private static byte[] md5(byte... input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private static byte[] getDataFromPathOrBuffer(Object pathOrBuffer) throws IOException {
        byte[] data;
        if (pathOrBuffer instanceof byte[]) {
            data = (byte[]) pathOrBuffer;
        } else {
            data = Files.readAllBytes((Path) pathOrBuffer);
        }

        return data;
    }

    public static void verifyKeys(byte[] aesKey, byte[] hmacKey) throws Exception {
        if (!Arrays.equals(BOSS_AES_KEY_HASH, md5(aesKey))) {
            throw new Exception("Invalid BOSS AES key");
        }

        if (!Arrays.equals(BOSS_HMAC_KEY_HASH, md5(hmacKey))) {
            throw new Exception("Invalid BOSS HMAC key");
        }
    }

    private static BossContainer decryptWiiU(Object pathOrBuffer, byte[] aesKey, byte[] hmacKey) throws Exception {
        verifyKeys(aesKey, hmacKey);

        final byte[] data = getDataFromPathOrBuffer(pathOrBuffer);
        final int hashType = ByteArrayUtils.readUnsignedShort(data, 0xA);
        if (hashType != (2 & 0xFFFF)) {
            throw new Exception("Unknown hash type");
        }

        final byte[] IV = ByteArrayUtils.concat(Arrays.copyOfRange(data, 0xC, 0x18), new byte[]{0x00, 0x00, 0x00, 0x01});
        final SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
        final IvParameterSpec ivSpec = new IvParameterSpec(IV);
        final Cipher decipher = Cipher.getInstance("AES/CTR/NoPadding");
        decipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        final byte[] decrypted = ByteArrayUtils.concat(decipher.update(Arrays.copyOfRange(data, 0x20, data.length)),
                decipher.doFinal());
        final byte[] hmac = Arrays.copyOfRange(decrypted, 0, 0x20);
        final byte[] content = Arrays.copyOfRange(decrypted, 0x20, decrypted.length);
        final byte[] calculatedHmac = calculateHMAC(content, hmacKey);
        if (!Arrays.equals(calculatedHmac, hmac)) {
            throw new Exception("Content HMAC check failed");
        }
        return new BossContainer(hashType, IV, hmac, content);
    }

    private static byte[] calculateHMAC(byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        final Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        mac.update(data);
        return mac.doFinal();
    }

    public static class BossContainer {

        public final int hashType;
        public final byte[] iv;
        public final byte[] hmac;
        public final byte[] content;

        private BossContainer(int hashType, byte[] IV, byte[] hmac, byte[] content) {
            this.hashType = hashType;
            iv = IV;
            this.hmac = hmac;
            this.content = content;
        }
    }


    public static BossContainer decrypt(Object pathOrBuffer, byte[] aesKey, byte[] hmacKey) throws Exception {
        final byte[] data = getDataFromPathOrBuffer(pathOrBuffer);

        final int magic = ByteArrayUtils.toInt(Arrays.copyOfRange(data, 0, 0x4));

        if (magic != BOSS_MAGIC) {
            throw new Exception("Missing boss magic");
        }


        final int version = ByteArrayUtils.toInt(Arrays.copyOfRange(data, 0x4, 0x8));

        if (version == BOSS_WUP_VER) {
            return decryptWiiU(data, aesKey, hmacKey);
        } else {
            throw new Exception("Unknown header version");
        }
    }

    public static Byml getStageByml() throws Exception {
        final String url = "https://npts.app.nintendo.net/p01/tasksheet/1/zvGSM4kOrXpkKnpT/schdat2?c=EU&l=en";
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(false);
        f.setValidating(false);
        final DocumentBuilder b = f.newDocumentBuilder();
        final URL url1 = new URL(url);
        final HttpsURLConnection urlConnection = (HttpsURLConnection) url1.openConnection();

        SSLBypass.allowAllSSL(urlConnection);
        urlConnection.addRequestProperty("Accept", "application/xml");
        Document doc = b.parse(urlConnection.getInputStream());
        doc.getDocumentElement().normalize();
        final Element files = (Element) doc.getDocumentElement().getElementsByTagName("Files").item(0);
        final Element file = (Element) files.getElementsByTagName("File").item(0);

        final URL rotationURL = new URL(file.getElementsByTagName("Url").item(0).getTextContent());
        final HttpsURLConnection conn = (HttpsURLConnection) rotationURL.openConnection();
        SSLBypass.allowAllSSL(conn);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = conn.getInputStream();
            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
            int n;

            while ( (n = is.read(byteChunk)) > 0 ) {
                baos.write(byteChunk, 0, n);
            }
        }
        catch (IOException e) {
            System.err.printf ("Failed while reading bytes from %s: %s", rotationURL.toExternalForm(), e.getMessage());
            e.printStackTrace ();
        }
        finally {
            if (is != null) { is.close(); }
        }



        final BossFileUtil.BossContainer test = BossFileUtil.decrypt(baos.toByteArray(), Config.instance().wiiuKeys.bossAesKey.getBytes(StandardCharsets.UTF_8), Config.instance().wiiuKeys.bossHmacKey.getBytes(StandardCharsets.UTF_8));
        final File boss = File.createTempFile("boss", ".byml");
        try (final FileOutputStream os = new FileOutputStream(boss)) {
            os.write(test.content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final BymlFile parse = BymlFile.parse(boss.getAbsolutePath());
        return Main.gson.fromJson(parse.toJson(), Byml.class);

    }

}
