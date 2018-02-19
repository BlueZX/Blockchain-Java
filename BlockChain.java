
import java.util.ArrayList;
import com.google.gson.GsonBuilder;


public class BlockChain{

	public static int difficulty = 5; //esta sera la dificultad de mineo

	public static ArrayList<Block> blockchain = new ArrayList<Block>(); //aqui almacenaremos nuestra cadena de bloques, uniendolos uno detras del otro

	public static void main(String[] args){
		Block genesisBlock = new Block("este es el primer bloque","0"); //como el bloque genesis no tiene un bloque anterior, ya que es el primero, el prevHash lo pondremos como 0
		addBlock(genesisBlock); //añadimos el genesisBlock a la blockchain...

		Block secondBlock = new Block("segundo Bloque", blockchain.get(blockchain.size()-1).hash);
		addBlock(secondBlock);

		Block thirdBlock = new Block("tercer Bloque", blockchain.get(blockchain.size()-1).hash);
		addBlock(thirdBlock);

		//solo es para ver la blockchain en un Json
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe BlockChain: \n");
		System.out.println(blockchainJson);

	}

	//retornara falso, si el bloque no es valido
	public static Boolean isValid(){
		Block currentBlock;
		Block prevBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0','0');

		//recorre la blockchain
		for(int i=1;i<blockchain.size();i++){
			currentBlock = blockchain.get(i); //guarda de forma temporal el hash del bloque
			prevBlock = blockchain.get(i-1); //guarda de forma temporal el hash anterior del bloque

			//compara el hash calculado del bloque con uno que se caLculo ahora
			if(!currentBlock.hash.equals(currentBlock.calculateHash())){ 
				System.out.println("los hashes no son iguales");
				return false;
			}

			//compara el hash anterior con el hash anterior temporal
			if(!prevBlock.hash.equals(currentBlock.prevHash)){
				System.out.println("Los hashes previos no son iguales");
				return false;
			}

			//con este if vemos si el bloque fue minando
			if(!currentBlock.hash.substring(0, difficulty).equals(hashTarget)){
				System.out.println("This block hasn't been mined");
				return false;
			}

		}
		return true;
	}

	//con este metodo minamos y a su vez agregamos el bloque a la blockchain
	public static void addBlock(Block block) {
		System.out.println("Trying to Mine block "+ (blockchain.size()+1) + " ... ");
		block.mineBlock(difficulty); //comenzamos a minar el bloque, para que este sea un bloque valido
		blockchain.add(block); 
	}
}