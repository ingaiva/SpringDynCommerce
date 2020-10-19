package data.repositorys;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import data.entitys.*;

@CrossOrigin("*")
@RepositoryRestResource
public interface RepoCategorieProduit  extends JpaRepository<CategorieProduit, Long>  {
//	static String queryProdWithDependency="select distinct c.id, c from CategorieProduit c JOIN FETCH "
//			+ " c.produits p JOIN FETCH p.photos JOIN FETCH p.promoActivations pa JOIN FETCH pa.promo  "
//			+ " JOIN FETCH p.lstInfoComp lst JOIN FETCH lst.lignesInfosComp";
	static String queryProdWithDependency="select distinct c from CategorieProduit c LEFT JOIN FETCH "
			+ " c.produits p ";
	@Query( queryProdWithDependency )
	public Set<CategorieProduit> getCategoriesWithDependency();
	
	@Query( queryProdWithDependency + " where c.id =:x")
	public CategorieProduit getCategoriesWithDependencyById(@Param("x") Long idCategorie);
		
}
