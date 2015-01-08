package element;

import interaction.Actions;
import interaction.Deplacements;
import interaction.DuelBasic;
import interfaceGraphique.VueElement;
import utilitaires.Calculs;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.Hashtable;

/**
 * Created by clement on 08/01/2015.
 */
public class Sniper extends Personnage{

	private Hashtable<Integer, VueElement> listePersonnagesVus = new Hashtable<Integer, VueElement>();


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
	public Sniper(String nom, int force, int charisme, int hp, int vitesse, int defense) {
		super(nom, force, charisme, hp, vitesse, defense);
	}

	public void strategie(VueElement ve, Hashtable<Integer, VueElement> voisins, Integer refRMI) throws RemoteException
	{
		Actions actions = new Actions(ve, voisins); //je recupere les voisins (distance < 10)
		Deplacements deplacements = new Deplacements(ve, voisins);

		System.out.println(ve.getPoint());

		if (0 == voisins.size()) { // je n'ai pas de voisins, j'erre
			VueElement cible=personnagePlusEloigne(ve);
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

		else {
			//On mémorise tous les personnages vus.
			this.listePersonnagesVus.putAll(voisins);

			VueElement cible = Calculs.chercherElementProche(ve, voisins);

			int distPlusProche = Calculs.distanceChebyshev(ve.getPoint(), cible.getPoint());

			int refPlusProche = cible.getRef();
			Element elemPlusProche = cible.getControleur().getElement();

			// dans la meme equipe ?
			boolean memeEquipe = false;

			if (elemPlusProche instanceof Personnage) {
				memeEquipe = (this.getLeader() != -1 && this.getLeader() == ((Personnage) elemPlusProche).getLeader()) || // meme leader
						this.getLeader() == refPlusProche || // cible est le leader de this
						((Personnage) elemPlusProche).getLeader() == refRMI; // this est le leader de cible
			}

			if (distPlusProche <= 5) { // si suffisamment proches
				if (elemPlusProche instanceof Personnage && isDanger((Personnage) elemPlusProche)) { //ennemi dangereux
					fuir(ve,cible,deplacements);
				}
				else
				{
					actions.interaction(ve.getRef(),cible.getRef(),ve.getControleur().getArene());
				}
			}
			else { // sinon on tire sur l'ennemi le plus éloigné voisins
				cible = personnagePlusEloigne(ve);
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


	@Override
	protected boolean isDanger(Personnage personnage) {
		// si on peut être converti ou convertir
		if (personnage.getForce() < this.getCharisme() || personnage.getCharisme() > this.getForce()) return false;

		//si le perso peut nous tuer d'un coup
		if (super.isDanger(personnage)) return true;

		final int CRITICAL_RANGE = 11;

		// cette valeur détermine les points de vie restants en cas de coup critique
		int thisMinHPApresAttaque = (CRITICAL_RANGE + personnage.getForce()) * (1 - this.getDefense() / 100);
		int thisMaxHPApresAttaque = (int) ((personnage.getForce()) * (1 - (double) this.getDefense() / 100));

		// si le personnage peut nous faire très mal mais pas tuer en un coup critique
		// et si on peut le convertir
		if (thisMaxHPApresAttaque > 0 && thisMinHPApresAttaque <= CRITICAL_RANGE) {
			return false;
		}

		// si on peut tuer le personnage d'un coup
		if (personnage.getHP() - (CRITICAL_RANGE + this.getForce()) * (1 - personnage.getDefense() / 100) < 0)
			return false;

		return true;
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
				if(!ve.equals(vueElement) && (distance = Calculs.distanceChebyshev(vueElement.getPoint(),ve.getPoint())) > distanceMax) {
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
}
