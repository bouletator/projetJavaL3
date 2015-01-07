package interfaceGraphique;
import java.awt.Point;
import java.io.Serializable;
import java.rmi.RemoteException;

import controle.IConsole;

/**
 * Definit la representation d'un element par sa vue : coordonnees, reference dans l'annuaire RMI, console, message... 
 */
public final class VueElement implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/**
	 * Reference de l'element sur le serveur.
	 */
	private int ref;
	/**
	 * Position de l'element.
	 */
	private Point point;
	/**
	 * Controleur de l'element.
	 */
	private final IConsole ctr;
	/**
	 * Message communique par l'element.
	 */
	private String phrase;
	/**
	 * Duree de vie (max 10 minutes).
	 */
	private int TTL=60*10;

	/**
	 * Constructeur
	 * @param ref la reference sur le serveur
	 * @param point la position initiale
	 * @param c le controleur auquel l'element est associe
	 * @param phrase le message a communiquer
	 */
	public VueElement(int ref, Point point, IConsole c, String phrase) {
		this.ref = ref;
		this.point = point;
		this.ctr = c;
		this.phrase = phrase;
	}

	/**
	 * Constructeur
	 * @param ref la reference sur le serveur
	 * @param point la position initiale
	 * @param c le controleur auquel l'element est associe
	 * @param phrase le message a communiquer
	 * @param tTL le temps de vie
	 */
	public VueElement(int ref, Point point, IConsole c, String phrase, int tTL) {
		this.ref = ref;
		this.point = point;
		this.ctr = c;
		TTL = tTL;
	}

	/**
	 * Renvoie la reference de l'element sur le serveur
	 */
	public int getRef() {
		return ref;
	}

	/**
	 * Renvoie la duree de vie de l'element
	 */
	public int getTTL() {
		return TTL;
	}

	/**
	 * Mets a jour la duree de vie de l'element
	 * @param tTL le nouveau temps de vie
	 */
	public void setTTL(int tTL) {
		TTL = tTL;
	}

	/**
	 * Renvoie le point sur lequel l'element se trouve
	 */
	public Point getPoint() {
		return point;
	}

	/**
	 * Mets a jour la position sur laquelle l'element se trouve
	 */
	public void setPoint(Point point) {
		this.point = point;
	}

	/**
	 * Renvoie le controleur auquel l'element est associe
	 */
	public IConsole getControleur(){
		return this.ctr;
	}

	/**
	 * Renvoie le message communique par l'element
	 */
	public String getPhrase(){
		return this.phrase;
	}
	
	/**
	 * Reinitialise le message a communiquer par l'element
	 * @param phrase le nouveau message 
	 */
	public void setPhrase(String phrase){
		this.phrase = phrase;
	}

	/**
	 * Diminue la duree de vie d'une unite
	 */
	public void decrTTL() {
		if (TTL>0) 
			TTL--;
	}	
	
	/**
	 * Clone la representation courante de l'element
	 */
	public VueElement clone() {
		return new VueElement( ref,  point, ctr, phrase, TTL);
	}

	/**
	 * Affiche l'etat courant de l'element
	 */
	public String afficher(){
		try{
			return this.ctr.afficher();
		}
		catch(RemoteException e){
			e.printStackTrace();
			return "";
		}
	}	
}
