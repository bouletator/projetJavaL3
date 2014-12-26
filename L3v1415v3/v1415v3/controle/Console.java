package controle;

import interfaceGraphique.VueElement;

import java.awt.Point;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
//import java.util.Random;















import element.*;
import serveur.IArene;

/**
 * 
 * Se connecte au serveur avec un Element et sa VueElement.
 * "run" permet a l'Element/VueElement de se deplacer
 *
 */
public class Console extends UnicastRemoteObject implements IConsole {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Port 5099 par defaut pour communiquer avec l'arene.
	 */
	private int port;
	/**
	 * Adresse IP du serveur de l'arene, localhost si en local.
	 */
	private String ipArene;
	/**
	 * Arene (serveur) avec lequel le controleur communique.
	 */
	private IArene arene = null;
	/**
	 * Vue de l'element (pour l'interface graphique).
	 */
	private VueElement ve = null;
	/**
	 * Element pour lequel le controleur est cree.
	 */
	private final Element elem;
	/**
	 * Vues des voisins sur l'interface graphique.
	 */
	private Hashtable<Integer, VueElement> voisins;
	/**
	 * Reference attribuee par le serveur a la connexion.
	 */
	private int refRMI;
	
	
	/**
	 * Constructeur
	 * @param elem l'element pour lequel le controleur est cree
	 * @param dx la position initiale de l'element sur l'ordonnee (interface graphique)
	 * @param dy la position initiale de l'element sur l'abscisse (interface graphique)
	 * @param port numero de port de l'arene 5099 par defaut 
	 * @throws RemoteException
	 */
	public Console(Element elem, int dx, int dy, int port, String ipArene) throws RemoteException {
		 //appel au constructeur de la super-classe -> il peut etre implicite
		super();
		this.port = port;
		this.ipArene = ipArene;
		//initialisation de l'element pour lequel le controleur est cree
		this.elem=elem;
		try {	
			//position initiale aleatoire ou via constructeur dx, dy
			//Random r=new Random();
			//Point pos = new Point(r.nextInt(100),r.nextInt(100));
			Point pos = new Point(dx,dy);
			
			//initialisation des voisins (vide avant la connexion)
			voisins = new Hashtable<Integer,VueElement>();
			
			//preparation connexion au serveur
			//handshake/enregistrement RMI
			System.out.println("rmi://"+this.ipArene+":"+this.port+"/Arene");
			arene = (IArene) java.rmi.Naming.lookup("rmi://"+this.ipArene+":"+this.port+"/Arene");
			arene.toString();
			
			//initialisation de la reference du controleur sur le serveur
			//La console devient "serveur" pour les methodes de IConsole 
			//lancer l'annuaire rmi en tant que serveur. A faire une seule fois par serveur de console pour un port donne
			//doit rester "localhost"
			this.refRMI = arene.allocateRef();
			int portServeur = this.port+refRMI;
			java.rmi.registry.LocateRegistry.createRegistry(portServeur);
			Naming.rebind("rmi://localhost:"+(portServeur)+"/Console"+refRMI,this);
		
			
			//initialisation de la vue sur l'element
			ve=new VueElement(refRMI, pos, this, "Atterrissage...");
						
			//connexion a l'arene pour lui permettre d'utiliser les methodes de IConsole
			String ipConsole =InetAddress.getLocalHost().getCanonicalHostName();
			//String ipConsole =InetAddress.getLocalHost().getHostAddress();
			//String ipConsole =InetAddress.getHostAddress();
			//System.out.println("ipConsole="+ipConsole);
			
			arene.connect(ve,ipConsole);
			
			//affiche message si succes
			System.out.println("Console connectee ["+refRMI+"]");
 		} catch (Exception e) {
  			System.out.println("Erreur : la console n'a pas pu etre creee !\nCause : "+e.getCause());
  			e.printStackTrace();
 		}
	}


	/**
	 * Permet au serveur de faire "jouer" un tour a l'element.
	 * Calcule ses voisins (donnes par le serveur), cherche le plus proche, s'il est a proximite, lance l'interaction sinon se dirige vers lui (s'il existe un plus proche)
	 * Cette methode est execute chaque seconde  
	 */
	public void run() throws RemoteException {
		//si l'element auquel le controleur est associe est un personnage
		if(elem instanceof Personnage) {
			//decremente sa duree de vie
			ve.decrTTL(); 
			//mets a jour ses voisins 
			voisins = arene.voisins(ve.getPoint(), refRMI);
			//applique la strategie du personnage
            elem.strategie(this.ve, this.voisins, refRMI);
		}
	}

	/**
	 * Appelle par le serveur pour faire la MAJ du sujet.
	 */
	public VueElement update() throws RemoteException {
		VueElement aux=(VueElement) ve.clone();
		aux.setPhrase(ve.getPhrase()); 
		return aux;
	}

	/**
	 * Deconnexion du controleur du serveur.
	 * @param cause le message a afficher comme cause de la deconnexion
	 */
	public void shutDown(String cause) throws RemoteException {
		System.out.println("Console "+refRMI+" deconnectee : "+cause);
		System.exit(1);
	}

	public Element getElement() throws RemoteException {
		return elem;
	}
	
	public VueElement getVueElement() throws RemoteException {
		return ve;
	}
	
	public int getRefRMI() throws RemoteException{
		return refRMI;
	}
	
	public IArene getArene() throws RemoteException {
		return arene;
	}
	
	/**
	 * Identifie la phrase de l'element avec la ref RMI
	 */
	public String afficher() throws RemoteException{
		return "("+refRMI+")"+this.elem.toString();
	}

	//AB : on garde, c'est plus visuel que majAtt
	/**
	 * Enleve une partie de la vie de l'element.
	 */
	public void perdreVie(int viePerdue) throws RemoteException {
		elem.setVie(elem.getVie() - viePerdue);
		
		elem.parler("Ouch, j'ai perdu " + viePerdue + " points de vie. Il me reste " + this.elem.getVie() + " points de vie.",ve);
		System.out.println("Ouch, j'ai perdu " + viePerdue + " points de vie. Il me reste " + this.elem.getVie() + " points de vie.");
	}
	
	/**
	 * Modifie une partie ou toutes les caracteristiques de l'element.
	 */
	public void majCaractElement(Hashtable<String,Integer> nvCaract)throws RemoteException {
		elem.getCaract().putAll(nvCaract);
	}


	@Override
	public void changerLeader(IConsole leader) throws RemoteException {
		// ancien leader (s'il existe)
		int refOldLeader = ((Personnage) elem).getLeader();
		
		
		// si existant, enlever this de l'equipe de l'ancien leader (si existant)
		if(refOldLeader != -1) {
			arene.consoleFromRef(refOldLeader).enleverPersonnageEquipe(this);
		}
		
		// si on a un nouveau leader
		if(leader != null) { 			
			// ajouter this a l'equipe du nouveau leader (et modifier le leader)
			leader.ajouterPersonnageEquipe(this);			

			// ajouter toute l'equipe de this a l'equipe du nouveau leader (et modifier leur leader)
			for(int ref : ((Personnage) elem).getEquipe()) {
				leader.ajouterPersonnageEquipe(arene.consoleFromRef(ref));
			}
			
			// vider l'equipe de this
			((Personnage) elem).enleverTouteEquipe();
		}
	}


	@Override
	public void ajouterPersonnageEquipe(IConsole eq) throws RemoteException {
		// ajouter eq a l'equipe de this
		((Personnage) elem).ajouterEquipe(eq.getRefRMI());
		
		// changer le leader de eq vers this
		eq.setLeaderOnly(this);
	}


	@Override
	public void enleverPersonnageEquipe(IConsole eq) throws RemoteException {
		// enlever le leader de eq
		eq.clearLeaderOnly();

		// enlever eq de l'equipe de this
		((Personnage) elem).enleverEquipe(eq.getRefRMI());
	}


	@Override
	public void enleverTousPersonnagesEquipe() throws RemoteException {
		// effacer le leader de tous les personnages de l'equipe de this
		for(int r : ((Personnage) elem).getEquipe()) {
			arene.consoleFromRef(r).clearLeaderOnly();
		}
		
		((Personnage) elem).enleverTouteEquipe();
	}

	
	
	
	
	@Override
	public void setLeaderOnly(IConsole lead) throws RemoteException {
		((Personnage) elem).setLeader(lead.getRefRMI());
	}

	@Override
	public void clearLeaderOnly() throws RemoteException {
		((Personnage) elem).clearLeader();
	}
	
	
	
		
}







