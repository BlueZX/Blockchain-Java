import java.util.*;
import com.google.gson.GsonBuilder;
import java.security.*;


public class BlockChain{

	public static int difficulty = 3; //esta sera la dificultad de mineo
	public static float minTransaction = 0.1f; //el tamaño de la transacción minima

	public static ArrayList<Block> blockchain = new ArrayList<Block>(); //aqui almacenaremos nuestra cadena de bloques, uniendolos uno detras del otro
	public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>(); //ista de todas las transacciones no utilizadas

	public static Wallet walletA;
	public static Wallet walletB;

	public static Transaction genesisTransaction;

	public static void main(String[] args){

		//Setup Bouncey castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 

		//Creando nuevas Wallet
		walletA = new Wallet();
		walletB = new Wallet();
		Wallet coinbase = new Wallet();

		//Crear la transacción genesis, enviando 100 coin hacía la walletA
		genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
		genesisTransaction.generateSignature(coinbase.privateKey); //firmar manualmente la transacción genesis
		genesisTransaction.transactionId = "0"; //manualmente asignar el id de la transacción
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //añadir manualmente la salida de la transaccion
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //es importante almacenar nuestra primera transacción en la lista de UTXOs.

		System.out.println("Creando y Minando Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);

		//testing
		Block block1 = new Block(genesis.hash);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
		addBlock(block1);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		isValid();


		//solo es para ver la blockchain en un Json
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe BlockChain: \n");
		System.out.println(blockchainJson);

	}

	//retornara falso, si el bloque no es valido
	public static Boolean isValid(){

		Block currentBlock; 
		Block prevBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //una lista de trabajo temporal de transacciones no gastadas en un estado de bloque dado.
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		//recorre la blockchain y verifica los hashes
		for(int i=1; i < blockchain.size(); i++) {
			
			currentBlock = blockchain.get(i); //guarda de forma temporal el hash del bloque
			prevBlock = blockchain.get(i-1); //guarda de forma temporal el hash anterior del bloque

			//compara el hash calculado del bloque con uno que se caLculo ahora
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("los hashes no son iguales");
				return false;
			}

			//compara el hash anterior con el hash anterior temporal
			if(!prevBlock.hash.equals(currentBlock.prevHash) ) {
				System.out.println("Los hashes previos no son iguales");
				return false;
			}
			//Verifica si el hash fue resulto (minado)
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("Este bloque no pudo ser minado");
				return false;
			}
			
			//Recorre thru de las transacciones de la blockchains
			TransactionOutput tempOutput;
			for(int t=0; t <currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);
				
				if(!currentTransaction.verifySignature()) {
					System.out.println("#La firma en la transacción (" + t + ") no es valida");
					return false; 
				}
				if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Las entradas son nota igual a la salida de la transacción (" + t + ")");
					return false; 
				}
				
				for(TransactionInput input: currentTransaction.inputs) {	
					tempOutput = tempUTXOs.get(input.transactionOutputId);
					
					if(tempOutput == null) {
						System.out.println("#falta la entrada referenciada(" + t + ")");
						return false;
					}
					
					if(input.UTXO.value != tempOutput.value) {
						System.out.println("#La entrada de la transacción referenciada (" + t + ") el valor es invalido");
						return false;
					}
					
					tempUTXOs.remove(input.transactionOutputId);
				}
				
				for(TransactionOutput output: currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}
				
				if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
					System.out.println("#El destinatario de salida de la transacción (" + t + ") no es quien deberia ser");
					return false;
				}
				if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
					System.out.println("#La transacción de salida (" + t + ") 'cambio' no es remitente.");
					return false;
				}
				
			}
			
		}
		System.out.println("Blockchain es valida");
		return true;
	}

	//con este metodo minamos y a su vez agregamos el bloque a la blockchain
	public static void addBlock(Block block) {
		System.out.println("Intentando minar el bloque "+ (blockchain.size()+1) + " ... ");
		block.mineBlock(difficulty); //comenzamos a minar el bloque, para que este sea un bloque valido
		blockchain.add(block); 
	}
}