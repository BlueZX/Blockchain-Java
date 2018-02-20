import java.security.*;
import java.util.ArrayList;

public class Transaction{
	public String transactionId; //Esta variables es un hash de transacción
	public PublicKey sender; //direccion del remitente / public key
	public PublicKey reciepient; //dirección del destinatario
	public float value; //cantidad de fondos a transferir
	public byte[] signature; //nuestra firma crytografica para evitar que alguien gaste dinero de nuestra wallet
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	private static int sequence = 0; //Un recuento aproximado de cuántas transacciones se han generado

	// Constructor:
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs){
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}

	private String calculateHash(){
		sequence++; //incrementa
		return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value) + sequence);
	}

	//Firma todos los datos que no deseamos manipular.
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
		signature = StringUtil.applyECDSASig(privateKey,data);		
	}
	//Verifica que los datos que firmamos no han sido manipulados
	public boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
		return StringUtil.verifyECDSASig(sender, data, signature);
	}
}