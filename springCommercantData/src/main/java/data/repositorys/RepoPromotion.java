package data.repositorys;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import data.entitys.*;

@CrossOrigin("*")
@RepositoryRestResource
public interface RepoPromotion  extends JpaRepository<Promotion, Long>  {
	
	@Query( "select p from Promotion p JOIN FETCH p.promoActivations a JOIN FETCH a.produits"  )
	public Set<Promotion> getPromotionsWithDependency();
}
