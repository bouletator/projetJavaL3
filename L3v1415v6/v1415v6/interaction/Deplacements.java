package interaction;

import interfaceGraphique.VueElement;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Random;

import utilitaires.Calculs;
import controle.IConsole;

public class Deplacements implements IDeplacements {
	/**
	 * Vue de l'element (pour l'interface graphique).
	 */
    private final VueElement ve;
    /**
     * Ref RMI et les vues des voisins.
     */
    private Hashtable<Integer,VueElement> voisins;

    private boolean actionExecutee;
    
    public Deplacements(VueElement ve, Hashtable<Integer, VueElement> voisins) {
        this.ve = ve;
        
        if (voisins == null) {
        	this.voisins = new Hashtable<Integer,VueElement>();
        } else {
        	this.voisins = voisins;
        }        
        actionExecutee = false;
    }    
    
	/**
     * Deplace ce sujet d'une case en direction du sujet dont la reference est donnee en parametre
     * ref de soi-meme pour du sur-place, 0 pour errer et ref d'un voisin (s'il existe)
     * On ne manipule que la VueElement
     * @param ref la reference de l'element cible
     */    
    public void seDirigerVers(int ref) {
    	if(actionExecutee) {
    		System.err.println("Une action a deja ete executee pendant ce tour !");
    	} else {
        	Point pvers;
        	
            //si la cible est l'element meme, il reste sur place
            if (ref==ve.getRef()) return;
            
            //la reference est nulle : le personnage erre
            if (ref==0) { //initialisation aleatoie
                    Random r=new Random();
                    pvers=new Point(r.nextInt(100), r.nextInt(100));     
            } else {//sinon la cible devient le point sur lequel se trouve l'element le plus proche
                pvers=voisins.get(ref).getPoint();
            }
            
            //si l'element n'existe plus (cas posible: deconnexion du serveur), le point reste sur place
            if (pvers == null) return;
            
            seDirigerVers(pvers);
    	}
    }

	/**
     * Deplace ce sujet d'une case en direction de la case specifiee.
     * On ne manipule que la VueElement
     * @param pvers case cible
     */
    public void seDirigerVers(Point pvers) {
    	if(actionExecutee) {
    		System.err.println("Une action a deja ete executee pendant ce tour !");
    	} else {
    		
    		IConsole console = ve.getControleur();
            Point dest = ve.getPoint();
        	int vitesse = 1;
        	try {
				vitesse = console.getElement().getCaract("vitesse");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			for(int i = 0; i < vitesse; i++){
				if(dest != pvers){
					dest = calculProchaineCase(dest, pvers);
				}
			}
	        
	        //si le point destination est libre
	        if (Calculs.caseVide(dest, voisins)) {
	            //l'element courant se deplace
	            ve.setPoint(dest);
	        } else {
	            //cherche la case libre la plus proche dans la direction de la cible
	            Point top = Calculs.meilleurPoint(ve.getPoint(),dest,voisins);
	            //deplace l'element courant sur celle-la
	            ve.setPoint(top);
	        }	        
	        actionExecutee = true;
    	}
    }
    
    /**
     * Calcule le point autour de dep pour aller vers arr
     * @param dep point de depart
     * @param arr point d'arrivée
     * @return point de destination
     */
    private Point calculProchaineCase(Point dep, Point arr){
    	//calcule la direction pour atteindre la ref (+1/-1 par rapport a la position courante)
    	int dx=(int) (arr.getX()-dep.x);
	   
    	if (dx!=0) {
    		dx=dx/Math.abs(dx);
    	}	   
    	int dy=(int) (arr.getY()-dep.y);
	   
    	if (dy!=0) {
    		dy=dy/Math.abs(dy);
    	}
    	//instancie le point destination
    	Point dest = new Point(dep.x+dx,dep.y+dy);
    	return(dest);
    }

}
