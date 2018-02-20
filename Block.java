import java.util.Date;
import java.util.*;

public class Block{

	public String hash; //la firma digital de este bloque
	public String prevHash; //firma digital del bloque anterior
	public String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); //nuestros datos serán un mensaje simple.
	private long timeStamp; //un numero en milisegundos desde 01/01/1970
	private int nonce; //es el numero a buscar cuando se esta Minando un bloque

	//Block Contructor
	public Block(String prevHash){
		this.prevHash = prevHash;
		this.timeStamp = new Date().getTime();

		this.hash = calculateHash(); //hacer esto luego de establecer los otros valores
	}

	//calcula el nuevo hash basado en el contenido del bloque
	public String calculateHash(){
		String calculatedhash = StringUtil.applySha256(prevHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
		return calculatedhash;
	}

	//incrementa el valor del nonce hasta que se alcanza el resultado del hash
	public void mineBlock(int difficulty){
		merkleRoot = StringUtil.getMerkleRoot(transactions);
		String target = StringUtil.getDifficultyString(difficulty); //genera un String con dificultad * en este caso "0"

		while(!hash.substring(0, difficulty).equals(target)){
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("El bloque fue minado exitosamente!! :" + hash);
	}

	//añade las transacciones a este bloque
	public boolean addTransaction(Transaction transaction) {
		//procesa la transacción y verificar si es válida, a menos que el bloque sea bloque genético y luego lo ignora.
		if(transaction == null) return false;		
		if((prevHash != "0")) {
			if((transaction.processTransaction() != true)) {
				System.out.println("El proceso de transacción fallo. Descartado.");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Transacción completamente añadido al bloque");
		return true;
	}

}