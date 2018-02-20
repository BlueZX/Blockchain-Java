import java.security.*;
import java.util.*;

public class StringUtil {

	//Necesita una cadena y le aplica el algoritmo SHA256, y devuelve la firma generada como un String
	public static String applySha256(String input){
		try{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes("utf-8"));
			StringBuffer hexString = new StringBuffer();

			for(int i=0;i<hash.length;i++){
				String hex = Integer.toHexString(0xff & hash[i]);

				if(hex.length() == 1){
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	//toma la clave privada del remitente y la entrada (String), la firma y devuelve una matriz de bytes
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}
	
	//toma la firma, la clave pública y los datos (String) y devuelve verdadero en caso que la firma sea válida
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	//devuelve una cadena codificada de cualquier clave
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	//Vira en una serie de transacciones y devuelve una raíz de merkle.
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		int count = transactions.size();
		ArrayList<String> previousTreeLayer = new ArrayList<String>();

		for(Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.transactionId);
		}

		ArrayList<String> treeLayer = previousTreeLayer;

		while(count > 1) {
			treeLayer = new ArrayList<String>();
			for(int i=1; i < previousTreeLayer.size(); i++) {
				treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}

		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		
		return merkleRoot;
	}

	//Retorna el rango de dificultad del String, para compararlo con el hash. por ejemplo, dificultad de 5 retornara "00000"
	public static String getDifficultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}
}