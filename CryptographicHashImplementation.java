import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class that computes the fingerprint of a given message via SHA-512 bit standards.
 * @author Vlad Diaconu
 *
 */
public class CryptographicHashImplementation 
{
	private String fingerprint;
	private static final String algorithm = "SHA-512";
	
	public void hash(String input) 
	{
        try 
        {
          
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] inputDigest = digest.digest(input.getBytes());
            BigInteger digestToBigInteger = new BigInteger(1, inputDigest);

            String hash = digestToBigInteger.toString();
            fingerprint = hash;
        }
        catch (NoSuchAlgorithmException e) 
        {
            throw new RuntimeException(e);
        }
    }
	
	public String getFingerprint()
	{
		return fingerprint;
	}
}
