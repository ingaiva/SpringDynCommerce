
package data.repositorys;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import data.entitys.Produit;

@CrossOrigin("*")
@RepositoryRestResource
public interface RepoProduit extends JpaRepository<Produit, Long> {
	static String queryProdWithDependency="select DISTINCT p from Produit p LEFT  JOIN FETCH p.promoActivations pa  LEFT JOIN FETCH pa.promo  ";//LEFT JOIN FETCH p.lstInfoComp lst  LEFT  JOIN FETCH lst.lignesInfosComp
	
	@Query( queryProdWithDependency  )
	public Set<Produit> getProduitsWithDependency();
	
	@Query(queryProdWithDependency + " where p.id =:x" )
	public Produit getProduitWithDependency(@Param("x") Long idProduit);
		
	
	@Query(queryProdWithDependency + " where p.categorieProduit.id =:x" )
	public Set<Produit> getProduitWithDependencyByCategorie(@Param("x") Long idCategorie);
	
	@Query(queryProdWithDependency + " where p.categorieProduit.id IS NULL" )
	public Set<Produit> getProduitWithDependencyByCategorieNull();
}
