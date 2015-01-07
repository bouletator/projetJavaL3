package controle;

import interfaceGraphique.VueElement;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;

import serveur.IArene;
import element.Element;

/**
 * Represente le lien Element - Serveur
 */
public interface IConsole extends Remote {
	/**
	 * Arrete l'execution du controleur (thread)
	 * @param cause la raison pour laquelle l'arret est demande
	 * @throws RemoteException
	 */
	public void shutDown(String cause) throws RemoteException;
	
	/**
	 * Execute le thread de l'element
	 * @throws RemoteException
	 */
	public void run() throws RemoteException;
	
	/**
	 * Mise a jour de la vue de l'element auquel le controleur est associe
	 * @throws RemoteException
	 */
	public VueElement update() throws RemoteException;
		
	/**
	 * Renvoie l'element associe a la console
	 * @throws RemoteException
	 */
	public Element getElement() throws RemoteException;
	
	/**
	 * Renvoie la vue de l'element associe a la console 
	 * @throws RemoteException
	 */
	public VueElement getVueElement() throws RemoteException;
	
	
	/**
	 * Renvoie la ref RMI de la console 
	 * @throws RemoteException
	 */
	public int getRefRMI() throws RemoteException;
	
	/**
	 * Renvoie l'etat de l'element a afficher sur l'interface graphique
	 * @throws RemoteException
	 */
	public String afficher() throws RemoteException;
	
	
	public IArene getArene() throws RemoteException;
	
	/**
	 * Enleve une partie de la vie de l'element 
	 */
	public void perdreVie(int viePerdue) throws RemoteException;

	/**
	 * Modifie une partie ou toutes les caracteristiques de l'element
	 */
	public void majCaractElement(Hashtable<String,Integer> nvCaract) throws RemoteException;
	
	/**
	 * Modifie le leader (l'element courant et lead doivent etre des personnages).
	 * @param lead nouveau leader
	 * @throws RemoteException
	 */
	public void changerLeader(IConsole lead) throws RemoteException;

	/**
	 * Ajoute un personnage a l'equipe (l'element courant et lead doivent etre des personnages).
	 * @param eq nouvel equipier
	 * @throws RemoteException
	 */
	public void ajouterPersonnageEquipe(IConsole eq) throws RemoteException;

	/**
	 * Enleve un personnage de l'equipe (l'element courant et lead doivent etre des personnages).
	 * @param eq ancien equipier
	 * @throws RemoteException
	 */
	public void enleverPersonnageEquipe(IConsole eq) throws RemoteException;	

	/**
	 * Enleve tous personnages de l'equipe (l'element courant doit etre une personnages).
	 * @throws RemoteException
	 */
	public void enleverTousPersonnagesEquipe() throws RemoteException;	
	
	/**
	 * Change le leader (sans modifier son). Ne devrait pas etre utilise seul.
	 * @param lead nouveau leader 
	 * @throws RemoteException
	 */
	public void setLeaderOnly(IConsole lead) throws RemoteException;
	
	/**
	 * Supprime le leader (sans modifier son equipe). Ne devrait pas etre utilise seul.
	 * @throws RemoteException
	 */
	public void clearLeaderOnly() throws RemoteException;
}







