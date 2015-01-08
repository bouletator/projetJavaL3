package element;

import interaction.Actions;
import interaction.Deplacements;
import interfaceGraphique.VueElement;
import utilitaires.Calculs;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.Hashtable;

/**
 * Created by clement on 07/01/2015.
 */
public class JeTeVois extends Personnage {

	private Hashtable <Integer, VueElement> listePersonnagesVus = new Hashtable<Integer, VueElement>();


	/**
	 * Constructeur d'un personnage avec un nom et une quantite de force et de charisme.
	 * Au depart, le personnage n'a ni leader ni equipe.
	 *
	 * @param nom      nom
	 * @param force    force
	 * @param charisme charisme
	 * @param hp       hp
	 * @param vitesse  vitesse
	 * @param defense  defense
	 */
	public JeTeVois(String nom, int force, int charisme, int hp, int vitesse, int defense) {
		super(nom, force, charisme, hp, vitesse, defense);
	}

	public void strategie(VueElement ve, Hashtable<Integer,VueElement> voisins, Integer refRMI) throws RemoteException
	{
		Actions actions = new Actions(ve, voisins); //je recupere les voisins (distance < 10)
		Deplacements deplacements = new Deplacements(ve,voisins);
	
		if (0 == voisins.size()) { // je n'ai pas de voisins, j'erre
			System.out.println("J'ai pas de voisins");
			parler("Je cherche un autre objet...", ve);
			Point pointDir=null;
			// ON cherche une potion
			pointDir=trouverMeilleurePotion(ve);
			// S'il n'y en a pas alors on cherche un leader
			if(pointDir==null)
				pointDir=trouverMeilleurLeader(ve);
			// S'il n'y en a pas alors on cherche un ennemi faible
			if(pointDir==null)
				pointDir=trouverEnnemiFacile(ve);
			// S'il n'y en a pas alors on erre
			if(pointDir==null) {
				parler("J'erre...0",ve);
				deplacements.seDirigerVers(0); //errer si il n'y a pas de leader
			}
			else
				deplacements.seDirigerVers(pointDir);



		} else {
			//On mémorise tous les personnages vus.
			this.listePersonnagesVus.putAll(voisins);

			VueElement cible = Calculs.chercherElementProche(ve, voisins);

			int distPlusProche = Calculs.distanceChebyshev(ve.getPoint(), cible.getPoint());

			int refPlusProche = cible.getRef();
			Element elemPlusProche = cible.getControleur().getElement();

			// dans la meme equipe ?
			boolean memeEquipe = false;

			if(elemPlusProche instanceof Personnage) {
				memeEquipe = (this.getLeader() != -1 && this.getLeader() == ((Personnage) elemPlusProche).getLeader()) || // meme leader
						this.getLeader() == refPlusProche || // cible est le leader de this
						((Personnage) elemPlusProche).getLeader() == refRMI; // this est le leader de cible
			}

			if(distPlusProche <= 2) { // si suffisamment proches
				if(elemPlusProche instanceof Potion) { // potion
					// ramassage

					if(isBonnePotion(elemPlusProche)) {
						System.out.println("J'aime les potions");
						parler("Je ramasse une potion", ve);
						actions.ramasser(refRMI, refPlusProche, ve.getControleur().getArene());
						this.nombreTourPoursuite = 0;
					}

				} else { // personnage
					Personnage adversaire = (Personnage) elemPlusProche;
					if(isDanger(adversaire))
					{
						System.out.println("Danger très proche !!");
						fuir(ve, cible, deplacements);
						this.nombreTourPoursuite = 0;
					}
					else if(!memeEquipe && adversaire.getLeader() != -1 && !isDanger(adversaire) && (adversaire.getEquipe().size()) > this.getEquipe().size()) 
					{ // on cherche à se faire convertir par l'autre leader si son équipe est plus grande
						// si c'est un coequipier
						System.out.println("Je me cherche un leader");
						parler("Je vais voir ailleurs ! " + refPlusProche, ve);
						deplacements.seDirigerVers(refPlusProche);
						this.nombreTourPoursuite = 0;
					}
					else if(!memeEquipe && adversaire.getForce() < this.getCharisme())
					{
						System.out.println("Je cherche à convertir");
						parler("Je convertis quelqu'un qui n'est pas dangereux",ve);
						convertir(ve, cible, actions);
						this.nombreTourPoursuite = 0;
					}
					else if(memeEquipe && this.getLeader() == refPlusProche && dernierPersonnage(ve))
					{
						System.out.println("Je cherche à convertir mon leader");
						actions.interaction(ve.getRef(),refPlusProche,ve.getControleur().getArene());
						this.nombreTourPoursuite = 0;
					}
					else {
						System.out.println("J'erre lamentablement");
						deplacements.seDirigerVers(0); // errer
						this.nombreTourPoursuite = 0;
					}
				}
			} else { // si voisins, mais plus eloignes

				Point newDir;

				if(dernierPersonnage(ve) && this.getLeader()!=-1) { //On cherche à aller tuer notre leader si il ne reste qu'un personnage
					System.out.println("Je vais tuer mon leader !!!");
					parler("Je vais vers mon leader " + this.getLeader(), ve);
					deplacements.seDirigerVers(this.getLeader());
					this.nombreTourPoursuite = 0;

				}
				else if(cible.getControleur().getElement() instanceof Personnage && isDanger((Personnage) cible.getControleur().getElement()))
				{
					System.out.println("Je fuis lachement");
					parler("Je fuis un danger",ve);
					fuir(ve, cible, deplacements);
					this.nombreTourPoursuite = 0;
				}
				else if(cible.getControleur().getElement() instanceof Potion && isBonnePotion(cible.getControleur().getElement()))
				{
					System.out.println("J'aime toujours les potions");
					parler("Je vais chercher une potion que j'aime",ve);
					deplacements.seDirigerVers(refPlusProche);
					this.nombreTourPoursuite = 0;
				}
				else if(this.getLeader() == -1 && this.getEquipe().size() == 0 && (newDir = trouverMeilleurLeader(ve))!=null)//On est pas leader et on en cherche un
				{
					System.out.println("Je me cherche un leader à aimer");
					parler("Je vais me trouver un leader !", ve);
					deplacements.seDirigerVers(newDir);
					this.nombreTourPoursuite = 0;
				}
				//Si il est convertible
				else if(!memeEquipe  && !isDanger((Personnage)cible.getControleur().getElement()))//On est leader et on va convertir une personne qui n'est pas de notre équipe
				{
					System.out.println("Je cherche à convertir");
					if(this.nombreTourPoursuite == NOMBRE_TOUR_POURSUITE_MAX)//On ne poursuis pas indéfiniment
					{
						parler("Je ne poursuis plus mon voisin " + refPlusProche, ve);
						//voisins.remove(referencePoursuivie);
						fuir(ve, cible, deplacements);
						//refPlusProche = Calculs.chercherElementProche(ve,voisins).getRef();
						nombreTourPoursuite=0;
					}
					else {
						parler("Je vais vers le plus proche car il ne présente pas de danger", ve);
						deplacements.seDirigerVers(refPlusProche);
						this.referencePoursuivie = refPlusProche;
					}
					this.nombreTourPoursuite++;
				}
				else {
					System.out.println("J'aime l'inconnu !");
					parler("Je vais voir le perso le plus éloigné de moi", ve);

					VueElement persoEloigne = personnagePlusEloigne(ve);
					if(persoEloigne != null)
						deplacements.seDirigerVers(persoEloigne.getPoint());
					else
						deplacements.seDirigerVers(0);
					this.nombreTourPoursuite = 0;
				}
			}


		}
	}



	@Override
	protected boolean isBonnePotion(Element element) {
		Potion potion = (Potion) element;

		if(!super.isBonnePotion(element))return false;

		///On ne se soucie pas de la force

		// limite: defense maximale à perdre en prenant une potion
		final int DEFENSE_MAX_LOSS = -15;

		//si la potion diminue la vitesse ou le charisme ou trop de defense on ne la prend pas
		if (potion.getVitesse() < 0
				|| potion.getCharisme() < 0
				|| potion.getDefense() < DEFENSE_MAX_LOSS)
			return false;

		//si la potion augmente la vitesse, le charisme ou la défense en vérifiant les conditions précédentes alors on les prend
		if (potion.getVitesse() > 0 || potion.getCharisme() > 0 || potion.getDefense() > 0) return true;

		return false;
	}

	@Override
	protected boolean isDanger(Personnage personnage) {
		// si on peut être converti ou convertir
		if(personnage.getForce()<this.getCharisme() || personnage.getCharisme()>this.getForce()) return false;

		//si le perso peut nous tuer d'un coup
		if(super.isDanger(personnage))return true;

		final int CRITICAL_RANGE = 11;

		// cette valeur détermine les points de vie restants en cas de coup critique
		int thisMinHPApresAttaque = (CRITICAL_RANGE + personnage.getForce()) * (1 - this.getDefense()/100);
		int thisMaxHPApresAttaque = (int)((personnage.getForce()) * (1 - (double)this.getDefense()/100));

		// si le personnage peut nous faire très mal mais pas tuer en un coup critique
		// et si on peut le convertir
		if (thisMaxHPApresAttaque > 0 && thisMinHPApresAttaque <= CRITICAL_RANGE) {
			return false;
		}

		// si on peut tuer le personnage d'un coup
		if (personnage.getHP() - (CRITICAL_RANGE + this.getForce()) * (1 - personnage.getDefense()/100) < 0)
			return false;

		return true;
	}

	private Point trouverMeilleurePotion(VueElement vueElement) {

		//Pour tous les objets 'visibles'
		try {
			for (VueElement ve : vueElement.getControleur().getArene().getWorld()) {
				//Si c'est une potion
				if (ve.getControleur().getElement() instanceof Potion) {
					Potion pot = (Potion) ve.getControleur().getElement();
					if (isBonnePotion(pot))
						return ve.getPoint();
				}
			}
		}
		catch (RemoteException re){
			System.out.println("Remote exception Catched. Coming from VueElement 'visibility'");
			return null;
		}

		return null;
	}

	private Point trouverEnnemiFacile(VueElement vueElement) {

		//Pour tous les objets 'visibles'
		try {
			for (VueElement ve : vueElement.getControleur().getArene().getWorld()) {
				//Si c'est une potion
				if (ve.getControleur().getElement() instanceof Personnage
						&& ve.getRef()!=vueElement.getRef()) {
					Personnage per = (Personnage) ve.getControleur().getElement();
					if (!isDanger(per))
						return ve.getPoint();
				}
			}
		}
		catch (RemoteException re){
			System.out.println("Remote exception Catched. Coming from VueElement 'visibility'");
			return null;
		}

		return null;
	}



}
