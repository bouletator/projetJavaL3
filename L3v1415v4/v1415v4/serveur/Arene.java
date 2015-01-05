package serveur;

import interfaceGraphique.VueElement;

import java.awt.Point;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import controle.IConsole;
import element.Element;
import element.Potion;
import element.IElement;
import element.Personnage;
import utilitaires.Calculs;

/**
 * Definit le serveur de l'arene. Les elements, personnages, potions... Definit les methodes qui communiquent en RMI.
 */
public class Arene extends UnicastRemoteObject implements IArene, Runnable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Port a utiliser pour la connexion.
	 */
	private int port;
	/**
	 * Adresse IP de la machine hebergeant l'arene.
	 */
	private String ipName;
	/**
	 * Duree de vie du serveur en millisecondes.
	 */
	private long duree;
	/**
	 * Nombre d'elements connectes au serveur.
	 */
	private int compteur = 0;
	/**
	 * Elements connectes au serveur.
	 */
    private  Hashtable<Remote, VueElement> elements = null; 
    /**
     * Repertoire des refRMI et leur adresses IP.
     */
    private Hashtable<Integer, String> ipAddrConsoles = null;
    
    /**
     * Attributs pour gerer l'apparition des potions de facon "ordonnee" et aleatoire
     */
    private int nbElemMax = 100;
    private int compteurPotions = 0;
    private Hashtable<Remote, VueElement> potions = null;
    private Hashtable<Integer, String> ipAddrConsolesPotions = null;
    
    /**
     * Attributs pour gerer l'apparition des personnages de facon "ordonnee" et aleatoire
     */
    private int compteurPersonnages = 0;
    private Hashtable<Remote, VueElement> personnages = null;
    private Hashtable<Integer, String> ipAddrConsolesPersonnages = null;

	/**
	 * Constructeur 
	 * @param port le port de connexion
	 * @param ipName nom de la machine qui heberge l'arene
	 * @param maxElem  nombre d'element max a faire apparaitre en meme temps
	 * @throws Exception
	 */
	public Arene(int port, String ipName, int maxElem, long duree) throws Exception {
		super();
		this.port=port;
		this.ipName = ipName;
		this.duree = duree;
		Naming.rebind("rmi://"+this.ipName+":"+this.port+"/Arene",this);
		nbElemMax = maxElem;
		elements = new Hashtable<Remote,VueElement>();
		potions = new Hashtable<Remote,VueElement>();
		personnages = new Hashtable<Remote,VueElement>();
		ipAddrConsoles = new Hashtable<Integer,String>();
		ipAddrConsolesPotions = new Hashtable<Integer,String>();
		ipAddrConsolesPersonnages = new Hashtable<Integer,String>();
		compteurPotions = nbElemMax;
		compteurPersonnages = nbElemMax;
		new Thread(this).start();
	}

	/**
	 * @return the port
	 */
	public int getPort() throws RemoteException{
		return port;
	}


	/**
	 * @return the ipAddrConsoles
	 */
	public Hashtable<Integer, String> getIpAddrConsoles() throws RemoteException{
		return ipAddrConsoles;
	}



	/**
	 *  Fournit une reference (entiere) pour un nouvel element dans l'arene, compte les elements
	 *  la synchro permet de garantir l'acces e un seul thread a la fois au compteur++  
	 * @return reference (entiere) utilisee pour rmi, compter les elements 
	 * @throws RemoteException */
	public synchronized int allocateRef() throws RemoteException {
		compteur++;
		return compteur;
	}
	
	/**
	 * Boucle principale du thread serveur, supprime les elements non-vivants ou
	 * qui sont trop longs, et s'arrete au bout de 30 minutes (par defaut). 
	 */
	public void run() {
		TimeoutOp to;
		long tempsDepart = System.currentTimeMillis();
	    while(duree < 0 || (System.currentTimeMillis() - tempsDepart) < duree) {
			try {		
				synchronized(this) {	// on verouille le serveur durant un tour de jeu -> pas de connexion/deconnexion
					// a cet instant, pour chaque client connecte, on verifie s'il est en vie
					for(Enumeration<Remote> enu=elements.keys(); enu.hasMoreElements();) {
						// boucle de jeu
						Remote r = enu.nextElement();
						to = new TimeoutOp(r);
						to.join(4000);
						if (to.isAlive()) {
							to = null;
							System.out.println("Depassement du temps (element n+"+elements.get(r).getRef()+") !");
							elements.remove(r);
							((IConsole) r).shutDown("Presence sur l'arene trop long. Degage !");
						}
						else{
							IElement elem = ((IConsole) r).getElement();
							if(elem.getVie() <= 0){
								System.out.println(elem.getNom() + " est mort...");
								elements.remove(r);
								((IConsole) r).shutDown("Vous etes mort...");
							}
						}
					}
				}
				Thread.sleep(1000);	//dormir 'au plus' 1 seconde (difference temps execution est 1sec.) pour permettre connexion/deconnexion des qconsoles
			} catch (Exception e) {e.getMessage();}
		}
	    	//Apres une certaine duree (30mn par defaut), on ferme la "porte" RMI	
			try {
				unexportObject(this, true);
			} catch (NoSuchObjectException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Verifie que les capacites soient positives et inferieures au seuil
	 * @param r Remote
	 * @return true si element peut se connecter
	 * @throws RemoteException
	 */
	public boolean verif (Remote r) throws RemoteException {
		boolean res = true;
		
		Element e = (Element) ((IConsole) r).getElement();
		
        if (e instanceof Personnage) {
        	Personnage p = (Personnage) e;
        	
        	if(p.getLeader() != -1) {
        		res = false;
        	}
        	
        	if(!p.getEquipe().isEmpty()) {
        		res = false;
        	}
        }
        
        return res;
    }

	
	 /** Associe ("connecte") la representation d'un element en y associant un Remote, ajoute la representation d'un element dans l'arene 
	 * 	 * la synchro permet de garantir qu'on ne fait pas de nouvelle connection pdt un tour de jeu
	 * @param s vue (representation) de l'element 
	 * @param ipConsole adresse ip de la console qui se connecte
	 * @throws RemoteException
	 */
	  public synchronized void connect(VueElement s, String ipConsole) throws RemoteException {
			try {
				//creation du lien serveur-console
			    int portConsole = port+s.getRef(); //on associe un port unique a chaque console
			    System.out.println("adresse de la console "+"rmi://"+ipConsole+":"+portConsole+"/Console"+s.getRef());
			    Remote r=Naming.lookup("rmi://"+ipConsole+":"+portConsole+"/Console"+s.getRef());
			    System.out.println("adresse de la console "+"rmi://"+ipConsole+":"+portConsole+"/Console"+s.getRef());
			    
			    //Comportement selon Potion/Personnage
			    if(((IConsole) r).getElement() instanceof Potion) {
			    	//on met les potions dans la pile
			    	potions.put(r, s);
            		ipAddrConsolesPotions.put(s.getRef(), ipConsole);
            		compteurPotions--;
            		//si on n'a plus de place dans la pile, on les ajoute a l'arene
            		if(compteurPotions == 0){
            			//on ajoute les potions en attente sur l'arene
            			elements.putAll(potions);
            			ipAddrConsoles.putAll(ipAddrConsolesPotions);
            			//mise a nbMax pour le prochain largage
            			compteurPotions = nbElemMax;
            			potions = new Hashtable<Remote, VueElement>();
            			ipAddrConsolesPotions = new Hashtable<Integer, String>();
            		}
            	} else {
			    	if (verif(r)){
			    		//on met les personnages dans la pile
			    		personnages.put(r, s);
			    		ipAddrConsolesPersonnages.put(s.getRef(),ipConsole);
			    		compteurPersonnages--;
			    		//si on n'a plus de place dans la pile, on les ajoute a l'arene
			    		if(compteurPersonnages == 0){
			    		//on ajoute les potions en attente sur l'arene
			    		elements.putAll(personnages);
			    		ipAddrConsoles.putAll(ipAddrConsolesPersonnages);
			    		//mise a nbMax pour le prochain largage
            			compteurPersonnages= nbElemMax;
            			personnages = new Hashtable<Remote, VueElement>();
            			ipAddrConsolesPersonnages = new Hashtable<Integer, String>();
			    		}
			    	}
			    	else {
			    		((IConsole) r).shutDown("Tu as triche vilain ! Tu es vire !");	
			    	}	
			    }
			} catch (Exception e) {
				System.out.println("Erreur lors de la connexion d'un client (ref="+s.getRef()+") :");
				e.printStackTrace();
			}
			
		}

	/**
	 * Appele par l'IHM pour afficher une representation de l'arene
	 * via RMI, on envoie une copie (serialisee) du monde 
	 */
	public ArrayList<VueElement> getWorld() throws RemoteException {
		ArrayList<VueElement> aux=new ArrayList<VueElement>();
		for(VueElement s : elements.values()) {aux.add(s);}
		return aux; 
	}
	
	
	
	
	@Override
	public IConsole consoleFromRef(int ref) throws RemoteException {
		int p = port + ref;
	    String ip = ipAddrConsoles.get(ref);
	    Remote r = null;
	    
		try {
			r = Naming.lookup("rmi://" + ip + ":" + p + "/Console" + ref);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	     
	     return (IConsole) r;
	}

	/**
	 * Liste des reference des voisins (distance inferieure à la vision du personnage) et leurs coordonnees a partir d'une position
	 */
	public Hashtable<Integer, VueElement> voisins(Point pos,int ref)
			throws RemoteException {
		//Hashtable<Integer, Point> aux=new Hashtable<Integer, Point>();
		
		Hashtable<Integer,VueElement> aux = new Hashtable<Integer, VueElement>();

		int vision = ((Personnage) consoleFromRef(ref).getElement()).getVision();

		for(VueElement s : elements.values()) {
			if (((Calculs.distanceChebyshev(s.getPoint(), pos))<vision) & (s.getRef()!=ref)) {
				aux.put(s.getRef(), s);
			}
		}
		return aux;
	}
	
	/**
	 * Classe permettant de lancer une execution du client (run)
	 * dans un thread separe, pour pouvoir limiter son temps d'execution
	 * via un join(timeout)
	 *
	 */
	public class TimeoutOp extends Thread {
		private Remote r;
		TimeoutOp(Remote r) {this.r=r; start();}
		public void run() {
			try {
				((IConsole) r).run(); //on lance une execution
				elements.put(r,((IConsole) r).update()); //maj du serveur ac les infos du client, pourquoi clonage ??
				if (elements.get(r).getTTL()==0) {
					elements.remove(r);
					((IConsole) r).shutDown("Vous etes reste trop longtemps dans l'arene, vous etes elimine !");
				}
				
			} catch (Exception e) {
				//les exceptions sont inhibees ici, que ce soit une deconnection du client ou autre
				//en cas d'erreur, le client ne sera plus jamais execute
				//e.printStackTrace();
				elements.remove(r);
			} 
		}
	}	

	/**
	 * Renvoie le nombre de clients connectes
	 */
	public int countClients() {
		return elements.size();
	}
	
	public void printElements() {
		for(Remote r : elements.keySet()) {
			try {
				System.out.print(elements.get(r).getControleur().getElement() + " ");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println();
	}
		
	/**
	 * Supprime un element de la liste des elements connectes au serveur
	 * @param elem l'element a enlever
	 */
	public void supprimerElement(Remote elem) {
		elements.remove(elem);
	}
}
