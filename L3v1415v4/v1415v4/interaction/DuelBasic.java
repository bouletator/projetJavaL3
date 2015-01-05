package interaction;


import java.rmi.RemoteException;

import serveur.IArene;
import controle.IConsole;
import element.Personnage;

public class DuelBasic implements IDuel {
	
	/**
	 * Arene (pour retrouver la reference du leader.
	 */
	private IArene arene;

	/**
	 * Reference de l'attaquant.
	 */
	private IConsole attaquant; 
	/**
	 * Reference du defenseur.
	 */
	private IConsole defenseur;
	
	/**
	 * Constructeur
	 * @param arene arene
	 * @param att la reference de l'attaquant
	 * @param def la reference du defenseur
	 * @throws RemoteException
	 */
	public DuelBasic(IArene arene, IConsole att, IConsole def) throws RemoteException {
		this.arene = arene;
		this.attaquant = att;
		this.defenseur = def;
	}

	@Override
	public boolean memeLeader(IConsole per1, IConsole per2) throws RemoteException {
		return ((Personnage) per1.getElement()).getLeader() != -1 
				&& ((Personnage) per1.getElement()).getLeader() == ((Personnage) per2.getElement()).getLeader();
	}
	
	@Override
	public boolean isLeader(IConsole per1, IConsole per2) throws RemoteException {
		return ((Personnage) per2.getElement()).getLeader() == per1.getRefRMI();
	}
	
	@Override
	public void realiserCombat() throws RemoteException {
		Personnage pAtt = (Personnage) attaquant.getElement();
		Personnage pDef = (Personnage) defenseur.getElement();
	
		int attCharisme = pAtt.getCharisme();
		int attForce = pAtt.getForce();
		int defCharisme = pDef.getCharisme();
		int defForce = pDef.getForce();
	
		System.out.print("Duel entre " + attaquant.getRefRMI() + " et " + defenseur.getRefRMI() + " : ");
	
		try {
			if(memeLeader(attaquant, defenseur) || isLeader(attaquant, defenseur)) {
				// s'ils ont le meme leader ou que le defenseur est dans l'equipe de l'attaquant, rien ne se passe
				System.out.println("Rien ne se passe");
				
			} else if (isLeader(defenseur, attaquant)) {
				if(attCharisme > defCharisme) {
					// coup d'etat
					System.out.println(attaquant.getRefRMI() + " realise un coup d'etat contre " + defenseur.getRefRMI());
				} else {
					// coup d'etat echoue
					System.out.println("Rien ne se passe");
				}
				
			} else {
				// duel
				if(attCharisme > defForce) {
					// def dans l'equipe de att
					ajouterEquipe(attaquant, defenseur);
					
				} else if(attForce >= defCharisme) {
					// def frappe par att
					frappe(attaquant, defenseur);
					
				} else {
					
					if(defCharisme > defForce) {
						// att dans l'equipe de def
						ajouterEquipe(defenseur, attaquant);
						
					} else {
						// att tue par def
						frappe(defenseur, attaquant);
					}
					
				}
			}
		} catch (RemoteException e) {
			System.out.println("Erreur lors d'un duel :");
			e.printStackTrace();
		}
	}

	/**
	 * Effectue les actions lorsqu'un personnage ajoute une autre a son equipe.
	 * @param per personnage qui a ajoute a son equipe (ou celle de son leader)
	 * @param eq personnage a ajouter a l'equipe
	 */
	private void ajouterEquipe(IConsole per, IConsole eq) throws RemoteException {
		int leader = ((Personnage) per.getElement()).getLeader(); // ajouter au leader s'il y en a un

		//le personnage reçoit la determination de son enroleur
		eq.getElement().getCaract().put("determination", ((Personnage) per.getElement()).getDetermination());

		if(leader == -1) { // le personnage n'est pas dans une equipe
			eq.changerLeader(per);
			per.getElement().parler("J'ajoute " + eq.getRefRMI() + " a mon equipe", per.getVueElement());
			System.out.println(per.getRefRMI() + " ajoute a son equipe " + eq.getRefRMI());
			
		} else { // le personnage a un leader
			IConsole lead = arene.consoleFromRef(leader);
			
			eq.changerLeader(lead);
			
			lead.getElement().parler("J'ajoute " + eq.getRefRMI() + " a mon equipe (par " + per.getRefRMI() + ")", lead.getVueElement());
			System.out.println(leader + " ajoute a son equipe (par " + per.getRefRMI() + ") " + eq.getRefRMI());
		}
	}

	/**
	 * Effectue les actions lorsqu'un personnage tue un autre.
	 * @param per personnage qui tue
	 * @param frappe personnage tue
	 */
	private void frappe(IConsole per, IConsole frappe) throws RemoteException {
		//annonce de la frappe
		per.getElement().parler("Je frappe " + frappe.getRefRMI(), per.getVueElement());
		System.out.println(per.getRefRMI() + " frappe " + frappe.getRefRMI());

		//perte de vie en conséquence
		//Si la défense de frappe plus grande que l'attaque de per alors il ne se passe rien sinon frappe perd en vie
		//la quantitié de force moins sa défence
		if(((Personnage)frappe.getElement()).getDefense()<((Personnage)per.getElement()).getForce()) {
			frappe.perdreVie(((Personnage) per.getElement()).getForce() - ((Personnage) frappe.getElement()).getDefense());
		}

		//si le personnage meurt alors on l'enlève de tous les personnages de son équipe
		if (frappe.getElement().getVie() <= 0) frappe.enleverTousPersonnagesEquipe();
	}
}
