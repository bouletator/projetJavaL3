import interfaceGraphique.IHM;

/**
 * Test de l'interface graphique qui se connecte a l'Arene (apres lancement Arene, avant les Consoles)
 */
public class TestIHM {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port=5099;	//par defaut, port de l'arene=5099
		if (args.length!=0) port=Integer.parseInt(args[0]);
		
		String ipArene = "localhost";
		if (args.length!=0) if (args[1]!="") ipArene=args[1];
		
		IHM ihm = new IHM(port,ipArene);
		
		ihm.connect();
		//boucle infine, interrompue par l'utilisateur (IHM)
		while(true) {
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			ihm.repaint(); 
		}
	}

}
