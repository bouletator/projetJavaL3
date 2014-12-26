

import java.rmi.RemoteException;
import java.util.Random;

import controle.Console;
import element.Personnage;

/**
 * Test de la Console avec un Element qui s'ajoute a l'Arene (apres lancement Arene et IHM). A lancer en plusieurs exemplaires.
 */
public class TestPersonnageAlea {

	/**
	 * @param args
	 * @throws RemoteException 
	 */
	public static void main(String[] args) {
		
		try {
			int port=5099;	//par defaut, port de l'arene=5099
			if(args.length != 0) port = Integer.parseInt(args[0]);
			
			String ipArene = "localhost";
			if(args.length != 0) if(args[1] != "") ipArene = args[1];
	
			Personnage bidule = new Personnage("Truc", 100, 200);
			Random r = new Random();
			new Console(bidule, r.nextInt(100), r.nextInt(100), port, ipArene);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
