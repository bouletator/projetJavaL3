

import java.rmi.RemoteException;
import java.util.Random;

import controle.Console;
import element.JeTeVois;
import element.Personnage;

/**
 * Test de la Console avec un Element qui s'ajoute a l'Arene (apres lancement Arene et IHM). A lancer en plusieurs exemplaires.
 */
public class TestPersonnageAlea {

	public static void main(String[] args) {
		
		try {
			int port = 5099; // par defaut, 5099
			if (args.length > 0) {
				port = Integer.parseInt(args[0]);
			}
			
			String ipArene = "localhost"; // par défaut, localhost
			if (args.length > 1) { 
				ipArene = args[1];
			}

			Random r = new Random(System.currentTimeMillis());

			Personnage bidule = new JeTeVois("ISeeYou", 0,60,20,1,12);
			new Console(bidule, 15+r.nextInt(70), 15+r.nextInt(70), port, ipArene);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}