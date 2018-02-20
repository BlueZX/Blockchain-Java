
/* Esta clase se usará para hacer referencia a TransactionOutputs que aún no se han gastado */
public class TransactionInput {
	//transactionOutputId se usará para encontrar el TransactionOutput relevante, permitiendo a los mineros verificar su propiedad
	public String transactionOutputId; //Referencia a TransactionOutputs -> transactionId
	public TransactionOutput UTXO; //aqui se guardara el resutado de transaccion (siguiendo la convención de bitcoins)

	public TransactionInput(String transactionOutputId){
		this.transactionOutputId = transactionOutputId;
	}

}