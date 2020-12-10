package app.utility;

import java.util.ArrayList;
import java.util.List;

import data.entitys.Produit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class StatisticProduit {
	private List<Produit> produits = new ArrayList<Produit>();
	private boolean loaded=false;
	
	public boolean hasProduits() {
		return this.produits!=null && this.produits.size()>0;
	}
	
	public Float getTotal() {
		
		Float total = 0f;
		for (Produit p : produits) {
			if (p.getPrix()!=null && p.getQte()!=null) {
				
				total += p.getPrix() * p.getQte();
			}
		}
		return total;
	}
	
	public Float getTotalQte() {
		Float total = 0f;
		for (Produit p : produits) {
			if (p.getQte()!=null) {				
				total += p.getQte();
			}
		}
		return total;
	}
	
	public Produit findProduit(Produit curProduit) {
		for (Produit p : produits) {
			if(p.getId().equals(curProduit.getId()))
				return p;
		}
		return null;
	}
	
	public void addProduit(Produit curProduit, Float qte) {
		Produit produitInListe=findProduit(curProduit);
		if(produitInListe==null) {
			curProduit.setQte(qte);
			this.produits.add(curProduit);
		}
		else
			produitInListe.setQte(produitInListe.getQte()+qte);
	}
}
