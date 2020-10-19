package data.entitys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PanierWrapper implements Serializable {
	private static final long serialVersionUID = -8991190868440210252L;

	private List<Produit> produits = new ArrayList<Produit>();

	public static Float calcTotal(PanierWrapper panier) {
		return panier.getTotal();
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

	/*
	 * @Override public String toString() { return "PanierWrapper [" + (getTotal()
	 * != null ? "Total()=" + getTotal() : "") + "]"; }
	 */

}
