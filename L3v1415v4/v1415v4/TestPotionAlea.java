

import java.rmi.RemoteException;
import java.util.Random;

import controle.Console;
import element.Element;
import element.Potion;

public class TestPotionAlea {

	/**
	 * @param args arguments from call
	 * @throws RemoteException 
	 */
	public static void main(String[] args) {

		try {
			int port=5099;	//par defaut, port de l'arene=5099
			if (args.length!=0) port=Integer.parseInt(args[0]);
			String ipArene = "localhost";
			if (args.length!=0) if (args[1].contentEquals("")) ipArene=args[1];
			Element anduril = new Potion("Anduril", 25);
	
			Random r = new Random();
			new Console(anduril, r.nextInt(100),r.nextInt(100), port,ipArene);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
