package element;

import java.rmi.RemoteException;

/**
 * Methodes RMI specifiques aux personnages.
 *
 */
public interface IPersonnage extends IElement {
	
	
	/**
	 * Modifie le leader.
	 * @param ref reference du nouveau leader
	 */
	public void setLeader(int ref) throws RemoteException;
	
	/**
	 * Enleve le leader.
	 */
	public void clearLeader() throws RemoteException;
	
	/**
	 * Ajoute un personnage a l'equipe.
	 * @param ref reference du personnage a ajouter
	 */
	public void ajouterEquipe(int ref) throws RemoteException;
	
	/**
	 * Enleve un personnage de l'equipe.
	 * @param ref reference du personnage a enlever
	 */
	public void enleverEquipe(int ref) throws RemoteException;
	
	/**
	 * Vide toute l'equipe.
	 */
	public void enleverTouteEquipe() throws RemoteException;
}
