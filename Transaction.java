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

	//Retorna verdadero si la nueva transacción podría ser creada
	public boolean processTransaction(){

		if(verifySignature() == false){
			System.out.println("#La firma de la Transacción fallo");
			return false;
		}

		//reune entradas de transacciones (asegúrandose de que no se hayan gastado)
		for(TransactionInput i: inputs){
			i.UTXO = BlockChain.UTXOs.get(i.transactionOutputId);
		}

		//verifica si la transacción es valida
		if(getInputsValue() < BlockChain.minTransaction){
			System.out.println("#La transacción de entrada es muy pequeña: "+ getInputsValue());
		}

		//generar salida de las transacciones
		float leftover = getInputsValue() - value; //obtiene el valor de las entradas además el cambio sobrante (leftover) 
		transactionId = calculateHash();
		outputs.add(new TransactionOutput( this.reciepient, value,transactionId)); //envia el valor para el destinatario
		outputs.add(new TransactionOutput( this.sender, leftover,transactionId)); //envia lo sobrante 'cambio' devuelta al remitente

		//añade salidas a la lista no utilizada
		for(TransactionOutput o: outputs){
			BlockChain.UTXOs.put(o.id, o);
		}

		//remueve las transacciones de entrada desde las listas UTXO como gastadas  
		for(TransactionInput i : inputs){
			if(i.UTXO == null){
				continue; //Si la transaccion no fue encontrada salta esto.
			}
			BlockChain.UTXOs.remove(i.UTXO.id);
		}

		return true;
	}

	//retorna la suma de los valores de entradas (UTXOs)
	public float getInputsValue() {
		float total = 0;
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //if Transaction can't be found skip it 
			total += i.UTXO.value;
		}
		return total;
	}

	//retonamos la suma de los valores de salida
	public float getOutputsValue() {
		float total = 0;
		for(TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}
}