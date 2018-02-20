import java.util.Date;

public class Block{

	public String hash; //la firma digital de este bloque
	public String prevHash; //firma digital del bloque anterior
	private String data; //los datos del bloque, puede ser una transacción por ejemplo
	private long timeStamp; //un numero en milisegundos desde 01/01/1970
	private int nonce; //es el numero a buscar cuando se esta Minando un bloque

	public Block(String data, String prevHash){
		this.data = data;
		this.prevHash = prevHash;
		this.timeStamp = new Date().getTime();

		this.hash = calculateHash();
	}

	//calcula el nuevo hash basado en el contenido del bloque
	public String calculateHash(){
		String calculatedhash = StringUtil.applySha256(prevHash + Long.toString(timeStamp) + Integer.toString(nonce) + data);
		return calculatedhash;
	}

	//incrementa el valor del nonce hasta que se alcanza el resultado del hash
	public void mineBlock(int difficulty){
		String target = new String(new char[difficulty]).replace('\0', '0'); //genera un String con dificultad * en este caso "0"

		while(!hash.substring(0, difficulty).equals(target)){
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("El bloque fue minado exitosamente!! :" + hash);
	}

}