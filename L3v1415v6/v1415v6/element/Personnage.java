/**
 * 
 */
package element;


import interaction.Actions;
import interaction.Deplacements;
import interaction.DuelBasic;
import interfaceGraphique.VueElement;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;

import utilitaires.Calculs;

/**
 * Un personnage: un element possedant des caracteristiques et etant capable
 * de jouer une strategie. 
 */
public class Personnage extends Element implements IPersonnage {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Reference du leader de ce personnage, -1 si aucun.
	 */
	private int leader;
	
	/**
	 * Reference des personnages de l'equipe de ce personnage. 
	 * Vide si le leader n'est pas egal a -1.
	 */
	private ArrayList<Integer> equipe;


	protected Integer referencePoursuivie = -1;
	protected Integer nombreTourPoursuite = 0;
	protected Integer NOMBRE_TOUR_POURSUITE_MAX = 5;

	/**
	 * Constructeur d'un personnage avec un nom et une quantite de force et de charisme.
	 * Au depart, le personnage n'a ni leader ni equipe.
	 * @param nom
	 * @param force
	 * @param charisme
	 * @param hp
	 * @param vitesse
	 * @param defense
	 */
	public Personnage(String nom, int force, int charisme, int hp, int vitesse, int defense) {
		super(nom);
		ajouterCaract("force", force);
		ajouterCaract("charisme", charisme);
		
		/* -------------------------------------------
		 * Nouvelles stats
		 * ---------------------------------------- */
		ajouterCaract("hp", hp);
		ajouterCaract("vitesse", vitesse);
		ajouterCaract("defense", defense);
				
		leader = -1;
		equipe = new ArrayList<Integer>();
	}
	
	/* ----------------------------------------------------------
	 * ACCESSEURS
	 * ---------------------------------------------------------- */
	
	/** ---------------------------------------------------------
	 * 		public int getForce
	 *  ---------------------------------------------------------
	 * Retourne la valeur de force.
	 * @return bonus de force
	 *  ------------------------------------------------------ */
	public int getForce() {
		return getCaract("force");
	}
	
	
	/** ---------------------------------------------------------
	 * 		public int getCharisme
	 *  ---------------------------------------------------------
	 * Retourne la valeur de charisme.
	 * @return bonus de charisme
	 *  ------------------------------------------------------ */
	public int getCharisme() {
		return getCaract("charisme");
	}
	
	/** ---------------------------------------------------------
	 * 		public int getHP
	 *  ---------------------------------------------------------
	 * Retourne la valeur de la vie.
	 * @return bonus de vie
	 *  ------------------------------------------------------ */
	public int getHP() {
		return getCaract("hp");
	}
	
	/** ---------------------------------------------------------
	 * 		public int getVitesse
	 *  ---------------------------------------------------------
	 * Retourne la valeur de vitesse.
	 * @return bonus de vitesse
	 *  ------------------------------------------------------ */
	public int getVitesse() {
		return getCaract("vitesse");
	}
	
	/** ---------------------------------------------------------
	 * 		public int getDefense
	 *  ---------------------------------------------------------
	 * Retourne la valeur de defense.
	 * @return bonus de defense
	 *  ------------------------------------------------------ */
	public int getDefense(){
		return getCaract("defense");
	}

	
	/** ----------------------------------------------------------
	 * 		public int getLeader()
	 *  ----------------------------------------------------------
	 * Retourne le leader.
	 * @return leader (-1 si aucun)
	 *  ---------------------------------------------------------- */
	public int getLeader() {
		return leader;
	}

	
	/** ----------------------------------------------------------
	 * 		public ArrayList<Integer> getEquipe
	 *  ----------------------------------------------------------
	 * Retourne la liste des personnages de l'equipe.
	 * @return equipe
	 * -------------------------------------------------------- */
	public ArrayList<Integer> getEquipe() {
		return equipe;
	}
	

	@Override
	/** ----------------------------------------------------------
	 * 		public String toString
	 *  ----------------------------------------------------------
	 *  Cette méthode détermine l'affichage des différentes stats
	 *  ----------------------------------------------------------
	 * @return string
	 * ------------------------------------------------------- */
	public String toString(){
		String lead = (leader != -1)? ", leader: " + leader: "";
		String eq = "";
		
		if(!equipe.isEmpty()) {
			eq += ", equipe: ";
			
			for(int i = 0; i < equipe.size(); i++) {
				eq += equipe.get(i) + " ";
				
				if(i < equipe.size() - 1) {
					eq += " ";
				}
			}
		}
		
		return super.toString() + "[" + "HP : " + getHP() + ", For : " +  getForce() + ", Cha :" + getCharisme() 
				+  ", Def : " + getDefense() + ", Vit : " + getVitesse() +  lead + eq + "]";
	}
	
	
	
	
	@Override
	/** ----------------------------------------------------------
	 * 		public void setLeader()
	 *  ----------------------------------------------------------
	 * Cette méthode définie qui est le leader
	 * ------------------------------------------------------- */
	public void setLeader(int ref) throws RemoteException {
		leader = ref;
	}
	
	@Override
	/** ----------------------------------------------------------
	 * 		public void clearLeader()
	 *  ----------------------------------------------------------
	 * Cette méthode supprime le leader
	 * ------------------------------------------------------- */
	public void clearLeader() throws RemoteException {
		leader = -1;
	}
	
	@Override
	/** ----------------------------------------------------------
	 * 		public void ajouterEquipe(int ref)
	 *  ----------------------------------------------------------
	 * Cette méthode permet d'ajouter un personnage dans l'équipe
	 * -----------------------------------------------------------
	 * @param int ref
	 * ------------------------------------------------------- */
	public void ajouterEquipe(int ref) throws RemoteException {
		equipe.add((Integer) ref);
	}

	@Override
	/** ----------------------------------------------------------
	 * 		public void enleverEquipe(int ref)
	 *  ----------------------------------------------------------
	 * Cette méthode permet d'enlever un personnage dans l'équipe
	 * -----------------------------------------------------------
	 * @param int ref
	 * ------------------------------------------------------- */
	public void enleverEquipe(int ref) throws RemoteException {
		equipe.remove((Integer) ref);
	}

	@Override
	/** ----------------------------------------------------------
	 * 		public void enleverTouteEquipe()
	 *  ----------------------------------------------------------
	 * Cette méthode permet d'enlever toute l'équipe
	 * -------------------------------------------------------- */
	public void enleverTouteEquipe() throws RemoteException {
		equipe.clear();
	}
	
	

	
	
	
	
	
	
	
	
	
	
	/** --------------------------------------------------------------------
	 * 	public void strategie(VueElement ve, Hashtable<Integer,VueElement> voisins, Integer refRMI) throws RemoteException
	 *  --------------------------------------------------------------------
	 *  Classe à modifier pour la stratégie à adopter par votre personnage.
	 *  --------------------------------------------------------------------
	 * Met en place la strategie. On ne peut utiliser que les methodes de la 
	 * classe Actions.
	 * @param ve vue de l'element
	 * @param voisins element voisins de cet element
	 * @param refRMI reference attribuee a cet element par le serveur
	 * @throws java.rmi.RemoteException
	 */
	public void strategie(VueElement ve, Hashtable<Integer,VueElement> voisins, Integer refRMI) throws RemoteException {
		Actions actions = new Actions(ve, voisins); //je recupere les voisins (distance < 10)
		Deplacements deplacements = new Deplacements(ve, voisins);

		System.out.println(ve.getPoint());

		if (0 == voisins.size()) { // je n'ai pas de voisins, j'erre
			parler("Personne en mire...", ve);
			deplacements.seDirigerVers(0);

		}

		else {

			VueElement cible = Calculs.chercherElementProche(ve, voisins);

			int distPlusProche = Calculs.distanceChebyshev(ve.getPoint(), cible.getPoint());

			Element elemPlusProche = cible.getControleur().getElement();


			if (elemPlusProche instanceof Personnage) {
				if(cible!=null) {
					parler("Je vise et tire...En contournant les vérifs", ve);

					DuelBasic duelBasic = new DuelBasic(ve.getControleur().getArene(), ve.getControleur(),cible.getControleur());
					duelBasic.realiserCombat();
				}
				else
				{
					parler("Personne en mire...", ve);
					deplacements.seDirigerVers(0);
				}
			}

			if (distPlusProche <= 3) { // si suffisamment proches
				if (elemPlusProche instanceof Personnage && isDanger((Personnage) elemPlusProche)) { //ennemi dangereux
					fuir(ve,cible,deplacements);
				}
				else
				{
					parler("Je frappe...", ve);
					actions.interaction(ve.getRef(),cible.getRef(),ve.getControleur().getArene());
				}
			}
			else { // sinon on tire sur l'ennemi parmi les voisins
				if(cible!=null) {
					parler("Je vise et tire...", ve);
					DuelBasic duelBasic = new DuelBasic(ve.getControleur().getArene(), ve.getControleur(),cible.getControleur());
					duelBasic.realiserCombat();
				}
				else
				{
					parler("Personne en mire...", ve);
					deplacements.seDirigerVers(0);
				}
			}


		}
	}


	protected VueElement personnagePlusEloigne(VueElement vueElement) {
		int distanceMax = 0;
		VueElement elementLointain = null;
		int distance = 0;
		try {
			//Pour tous les objets 'visibles'
			for(VueElement ve : vueElement.getControleur().getArene().getWorld())
			{
				//Si le personnage est le plus éloigné
				if(ve.getControleur().getElement() instanceof Personnage && !ve.equals(vueElement) && (distance = Calculs.distanceChebyshev(vueElement.getPoint(),ve.getPoint())) > distanceMax) {
					distanceMax = distance;
					elementLointain = ve;
				}
			}
		}
		catch (RemoteException e) {
			return elementLointain;
		}


		return elementLointain;
	}


	protected boolean isBonnePotion(Element element){

		Potion potion =(Potion) element;

		//si la potion est déja ramassée on ne la prend pas
		if (potion.getVie() == 0) return false;

		//si la potion risque de tuer
		if (potion.getHP() + this.getHP() <= 0) return false;



		//si c'est un autre type de potion
		return true;
	}

	protected boolean isDanger(Personnage personnage) {
		// cette valeur détermine les points de vie restants en cas de coup critique
		int thisMaxHPApresAttaque = (int) ((personnage.getForce()) * (1.0 - (double)this.getDefense()/100.0));

		// si le personnage peut nous tuer d'un coup critique
		if (thisMaxHPApresAttaque <= 0) {
			return true;
		}

		return false;
	}



	protected void fuir(VueElement ve, VueElement veDanger, Deplacements deplacements) {
		int x=ve.getPoint().x, y=ve.getPoint().y;
		int xDan=veDanger.getPoint().x, yDan=veDanger.getPoint().y;

		Point newDir = new Point();

		//nouvelle direction
		newDir.setLocation(x+x-xDan, y+y-yDan);
		deplacements.seDirigerVers(newDir);
	}

	protected void convertir(VueElement per, VueElement targetVe, Actions actions) {
		try {
			actions.interaction(per.getRef(), targetVe.getRef(), per.getControleur().getArene());
		} catch (RemoteException e) {
			e.printStackTrace();
			return;
		}

	}


	protected boolean dernierPersonnage(VueElement vueElement) {
		int count = 0;
		try {
			//Pour tous les objets 'visibles'
			for(VueElement ve : vueElement.getControleur().getArene().getWorld())
			{
				//Si c'est un personnage
				if(ve.getControleur().getElement() instanceof Personnage) {
					Personnage per = (Personnage)ve.getControleur().getElement();
					//booleen vérifiant l'apartenance à la même équipe
					boolean memeEquipe = (leader != -1 && leader == per.getLeader()) || // meme leader
							leader == ve.getRef() || // cible est le leader de this
							per.getLeader() == this.leader; // this est le leader de cible
					//si ce n'est pas moi, que le perso n'est pas mort, et pas de la même équipe
					if(ve.getRef() != vueElement.getRef() && per.getVie()>0 && !memeEquipe) {
						//S'il y a plus d'un joueur qui n'est pas dans mon équipe alors on retourne false
						count++;
						if(count > 1) return false;
					}
				}
			}

		}
		catch (RemoteException re){
			System.out.println("Remote exception Catched. Coming from VueElement 'visibility'");
			return false;
		}

		if (count==1) return true;
		else return false;
	}

	protected Point trouverMeilleurLeader(VueElement vueElement) {


		int max=0;
		VueElement bestLeader = null;

		try {
			//Pour tous les objets 'visibles'
			for(VueElement ve : vueElement.getControleur().getArene().getWorld())
			{
				//Si c'est un personnage
				if(ve.getControleur().getElement() instanceof Personnage) {
					Personnage per = (Personnage)ve.getControleur().getElement();
					///Si le personnage est un leader
					if(ve.getRef() != vueElement.getRef() && per.getVie()>0 && per.getLeader()==ve.getRef()) {
						//Si son equipe est la plus grande
						if(per.getEquipe().size()>max){
							//c'est le max pour l'instant
							max = per.getEquipe().size();
							bestLeader = ve;
						}
					}
				}
			}

		}
		catch (RemoteException re){
			System.out.println("Remote exception Catched. Coming from VueElement 'visibility'");
			return null;
		}

		//Si tous les leaders ont la même taille d'équipe
		if(bestLeader == null)
			//Tant pis
			return null;
			//Sinon on renvoie la référence de celui qui a la plus grande équipe
		else return bestLeader.getPoint();

	}
}