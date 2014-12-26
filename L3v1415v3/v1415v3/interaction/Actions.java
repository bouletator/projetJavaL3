package interaction;

import interfaceGraphique.VueElement;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;

import serveur.IArene;
import controle.IConsole;

/**
 * Created by moi on 12/05/14.
 * Contient les actions basiques pouvant etre utilisees dans les strategies. 
 */
public class Actions implements IActions {

	/**
	 * Vue de l'element (pour l'interface graphique).
	 */
    private final VueElement ve;
    /**
     * Ref RMI et les vues des voisins.
     */
    private Hashtable<Integer,VueElement> voisins;
    /**
     * Initialise a faux, vrai si une action a deja ete executee.
     */
    private boolean actionExecutee;

    public Actions(VueElement ve, Hashtable<Integer, VueElement> voisins) {
        this.ve = ve;
        
        if (voisins == null) {
        	this.setVoisins(new Hashtable<Integer,VueElement>());
        } else {
        	this.setVoisins(voisins);
        }
        
        actionExecutee = false;
    }

    
	
	/**
	 * Appele par l'element. Permet a l'element ref1 (Personnage) de ramasser l'element ref2 (Potion), 
	 * qui modifie les caracteristiques du personnage.
	 * @param ref1 personnage ramassant la potion
	 * @param ref2 potion ramassee
	 * @param arene arene
	 */
	public void ramasser(int ref1, int ref2, IArene arene) throws RemoteException {
    	if(actionExecutee) {
    		System.err.println("Une action a deja ete executee pendant ce tour !");
    	} else {
			//recupere le combattant et la potion			
		    IConsole combattant = arene.consoleFromRef(ref1); 
		    IConsole potion = arene.consoleFromRef(ref2); 
		    
		    VueElement vCombattant = combattant.getVueElement();
		    Actions ramassage = new Actions(vCombattant,null);
		    
		    ramassage.ramasserPotion(potion, combattant);
	    	 
			actionExecutee = true;
    	}
	}
	
	
	/**
	 * Ramasse et utilise une potion sur un personnage specifiee. Ne doit pas 
	 * etre appele tel quel, mais est utilise dans {@code ramasser}.
	 * @param obj potion
	 * @param per personnage
	 * @throws RemoteException
	 */
	private void ramasserPotion(IConsole obj, IConsole per) throws RemoteException {
		Hashtable<String,Integer> valeursPerso = per.getElement().getCaract();
		Hashtable<String,Integer> valeursEq = obj.getElement().getCaract();
		Enumeration<String> enumCaract = valeursEq.keys();
		while (enumCaract.hasMoreElements()) {
			String s = enumCaract.nextElement();
			Integer val = per.getElement().getCaract(s);
			if (val!=null) {
				valeursPerso.put(s, val+valeursEq.get(s));
			}
			valeursEq.put(s, 0);//on vide toute la potion, meme si elle ne correspond pas aux caract. du perso ?
			obj.majCaractElement(valeursEq);
			per.majCaractElement(valeursPerso);
		}
    	//mets a jour l'etat de la potion comme ramassee (plus de vie)
    	obj.perdreVie(1);
	}

	/**
	 * Appele par le run de la console. Permet a l'attaquant (ref1) d'attaquer (executer une frappe) le defenseur (ref2).
	 * Les regles de combat s'appliquent (en fonction de la force, de la defense et de l'esquive).
	 * Les caracteristiques du defenseur sont mises a jour (pas d'impact sur attaquant)
	 * Les deux protagonistes ajoutent leur adversaire dans les elements deja vus. 
	 * @param ref1 attaquant
	 * @param ref2 defenseur
	 * @param arene arene
	 */
	public void interaction(int ref1, int ref2, IArene arene) throws RemoteException {
    	if(actionExecutee) {
    		System.err.println("Une action a deja ete executee pendant ce tour !");
    	} else {
			 //recupere l'attaquant et le defenseur
		    IConsole attaquant = arene.consoleFromRef(ref1);
		    IConsole defenseur = arene.consoleFromRef(ref2);
		     
		    //cree le duel
		    DuelBasic duel = new DuelBasic(arene, attaquant, defenseur);
			
			duel.realiserCombat(); 
			
			actionExecutee = true;
    	}
	}



	public Hashtable<Integer,VueElement> getVoisins() {
		return voisins;
	}



	public void setVoisins(Hashtable<Integer,VueElement> voisins) {
		this.voisins = voisins;
	}



	public VueElement getVe() {
		return ve;
	}	
	
	
}
