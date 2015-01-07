package interaction;

import java.rmi.RemoteException;

import controle.IConsole;

/**
 * Interface contenant les methodes passant par RMI pour un duel (un combat)
 * entre deux personnages (un attaquant, un defenseur). 
 */
public interface IDuel {
	

	/**
	 * Teste si les deux personnages ont des leaders et si ce sont les memes.
	 * @param per1 premier personnage
	 * @param per2 deuxieme personnage
	 * @return vrai si per1 et per2 ont des leaders identiques
	 * @throws RemoteException
	 */
	public boolean memeLeader(IConsole per1, IConsole per2) throws RemoteException; // TODO necessaire RMI ?
	
	/**
	 * Teste si le premier personnage est le leader du deuxieme.
	 * @param per1 premier personnage
	 * @param per2 deuxieme personnage
	 * @return vrai si per1 est le leader de per2
	 * @throws RemoteException
	 */
	public boolean isLeader(IConsole per1, IConsole per2) throws RemoteException; // TODO necessaire RMI ?
	
	/**
	 * Realise le combat entre deux personnages
	 * @throws RemoteException
	 */
	public void realiserCombat() throws RemoteException; 
}
