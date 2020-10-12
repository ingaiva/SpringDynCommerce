package data.repositorys;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import data.entitys.InfoCompProduit;

@CrossOrigin("*")
@RepositoryRestResource
public interface RepoInfoCompProduit  extends JpaRepository<InfoCompProduit, Long>  {
	static String queryWithDependency="select DISTINCT i from InfoCompProduit i  LEFT  JOIN FETCH i.lignesInfosComp ";//LEFT  JOIN FETCH i.produit p 
	@Query(queryWithDependency + " where i.produit.id =:x" )
	public List<InfoCompProduit> getByProduit(@Param("x") Long idProduit);
	
	@Query(queryWithDependency + " where i.id =:x" )
	public InfoCompProduit getOneWithDependency(@Param("x") Long id);
	
	@Transactional
	@Modifying
	@Query("delete from InfoCompProduit i where i.produit.id=:x")
	public void deleteByProduit(@Param("x") Long idParent);
}
