


import java.rmi.RemoteException;

import controle.Console;
import element.Personnage;

/**
 * Test de la Console avec un Element qui s'ajoute a l'Arene (apres lancement Arene et IHM). A lancer en plusieurs exemplaires.
 */
public class TestPersonnageCentre {

	/**
	 * @param args
	 * @throws RemoteException 
	 */
	public static void main(String[] args) {

		try {
			int port=5099;	//par defaut, port de l'arene=5099
			
			if (args.length!=0) port=Integer.parseInt(args[0]);
			
			String ipArene = "localhost";
			if (args.length!=0) if (args[1]!="") ipArene=args[1];
			
			Personnage bidule = new Personnage("Bidule", 100, 200);
			
			new Console(bidule, 40, 40, port, ipArene);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
