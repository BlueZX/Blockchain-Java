import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.*;

/* Generamos nuestras claves privadas y públicas en un KeyPair . 
Utilizaremos la criptografía de curva elíptica para generar nuestros KeyPairs
*/

public class Wallet{

	public PrivateKey privateKey;
	public PublicKey publicKey;

	public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>(); //solo UTXOs propiedad de esta wallet.

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

	//retorna saldo y almacena los UTXO propiedad de esta billetera en esto.UTXOs
	public float getBalance() {
		float total = 0;	
        for (Map.Entry<String, TransactionOutput> item: BlockChain.UTXOs.entrySet()){
        	TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) { //si la salida me pertenece (si las monedas me pertenecen)
            	UTXOs.put(UTXO.id,UTXO); //añadir a nuestra lista de transacciones no gastadas.
            	total += UTXO.value ; 
            }
        }
        return total;
    }

    //Genera y retorna una nueva transacción de esta wallet
    public Transaction sendFunds(PublicKey _recipient,float value ) {
		if(getBalance() < value) { //reunir el saldo y verificar los fondos.
			System.out.println("#No hay fondos suficientes para enviar la transacción. Transacción descartada.");
			return null;
		}

	    //crea un ArrayList de las entradas
	    ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

	    float total = 0;
	    for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			if(total > value) break;
		}
		
		Transaction newTransaction = new Transaction(publicKey, _recipient , value, inputs);
		newTransaction.generateSignature(privateKey);
		
		for(TransactionInput input: inputs){
			UTXOs.remove(input.transactionOutputId);
		}
		return newTransaction;
	}


}