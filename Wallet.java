import java.security.*;
import java.security.spec.ECGenParameterSpec;

/* Generamos nuestras claves privadas y públicas en un KeyPair . 
Utilizaremos la criptografía de curva elíptica para generar nuestros KeyPairs
*/

public class Wallet{

	public PrivateKey privateKey;
	public PublicKey publicKey;

	public Wallet(){
		generateKeyPair();	
	}

	public void generateKeyPair(){
		try{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec spec = new ECGenParameterSpec("prime192v1");

			keyGen.initialize(spec, random);

			KeyPair keyPair = keyGen.generateKeyPair();

			privateKey = keyPair.getPrivate();
			publicKey = keyPair .getPublic();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}