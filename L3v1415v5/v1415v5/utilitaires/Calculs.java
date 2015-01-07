package utilitaires;

import interfaceGraphique.VueElement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Classe regroupant quelques methodes utiles pour l'arene (distance, case vide,
 * elements voisins...).
 */
public class Calculs {

	/**
	 * Renvoie la distance Chebyshev entre deux points
	 * @param p1 le premier point
	 * @param p2 le deuxieme point
	 * @return un entier representant la distance
	 */
	public static int distanceChebyshev(Point p1, Point p2) {
		return Math.max(Math.abs(p1.x-p2.x),Math.abs(p1.y-p2.y));
	}

	/**
	 * Verifie si un element parmi les voisins occupe la position
	 * @param p une position   
	 * @param voisins des elements (VueElement)
	 * @return true si la case est vide et false si la case est occupe
	 */
	public static boolean caseVide(Point p, Hashtable<Integer,VueElement> voisins){
		boolean trouve=false;
		Point paux;
		Enumeration<VueElement> enump = voisins.elements();
		while (!trouve && enump.hasMoreElements()) {
			paux = enump.nextElement().getPoint();
			trouve = p.equals(paux); 
		}
		
		return !trouve;
	}
	
	/** 
	 * Renvoie le meilleur point a occuper par l'element courant dans la direction de la cible
	 * @param depart le point sur lequel se trouve l'element courant
	 * @param objectif le point sur lequel se trouve la cible
	 * @param voisins le positionement sur l'interface graphique de tous les elements en vie
	 * @return le meilleur point libre dans la direction de la cible
	 */
	public static Point meilleurPoint(Point depart, Point objectif, Hashtable<Integer,VueElement> voisins){
		//liste contenant tous les positions vers lesquelles l'element peut avancer
		ArrayList<Point> listePossibles = new ArrayList<Point>();		
		//pour chaque de 8 cases autour de lui
		for (int i=-1;i<=1;i++){
			for (int j=-1;j<=1;j++){
				if ((i!=0) || (j!=0))  {
 					//on ajoute la position (en valeur absolue pour eviter de sortir du cadre)
					listePossibles.add(new Point(Math.abs(depart.x+i),Math.abs(depart.y+j)));
				}
			}
		}
		//organise les points de la liste du plus pres vers le plus eloigne de la cible
		Collections.sort(listePossibles,new PointComp(objectif));		
		//cherche la case vide la plus proche de la cible
		boolean ok = false;
		int i=0;
		Point res=null;
		while (!ok & i<listePossibles.size()) {
			res = listePossibles.get(i);
			ok = caseVide(res,voisins);
			i++;
		}		
		//renvoie cette case
		return res;
	}

	/**
	 * Cherche l'element le plus proche vers lequel se didiger
	 * @param ve l'element courant
	 * @param voisins les elements voisins
	 * @return un hashmap contenant la distance a parcourir vers l'element le plus proche, son identifiant et sa vue 
	 */
	public static VueElement chercherElementProche(VueElement ve, Hashtable<Integer,VueElement> voisins){
		int distPlusProche = 100;
		int refPlusProche = 0;	
		for(Integer ref:voisins.keySet()) {
			Point target = voisins.get(ref).getPoint();
			if (Calculs.distanceChebyshev(ve.getPoint(),target)<distPlusProche) {
				distPlusProche = Calculs.distanceChebyshev(ve.getPoint(),target);
				refPlusProche = ref;
			}
		}		
		return voisins.get(refPlusProche);
	}
	
}
