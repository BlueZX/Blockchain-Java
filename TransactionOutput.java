import java.security.PublicKey;

/* Las salidas de las transacciones mostrarán el monto final enviado a cada parte de la transacción.
Estos, cuando se mencionan como entradas en nuevas transacciones, actúan como prueba de que tiene monedas para enviar.
*/
public class TransactionOutput {
	public String id;
	public PublicKey reciepient; //tambien conocido como el propietario de esta moneda
	public float value; //el monto de monedas que posees
	public String parentTransactionId; //la id de la transacción en la que se creo esta salida.
	
	//Constructor
	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
	}
	
	//comprueba si la moneda te pertenece
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == reciepient);
	}
	
}