package data.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import data.entitys.LigneInfoCompProduit;

@CrossOrigin("*")
@RepositoryRestResource
public interface RepoLigneInfoCompProduit   extends JpaRepository<LigneInfoCompProduit, Long> {
	
	@Transactional
	@Modifying
	@Query("delete from LigneInfoCompProduit l where l.infoComp.id=:x")
	public void deleteByInfoComp(@Param("x") Long idParent);
	
	@Transactional
	@Modifying
	@Query("delete from LigneInfoCompProduit l where l.infoComp.id in (select DISTINCT i.id from InfoCompProduit i  where i.produit.id=:x)")
	public void deleteByProduit(@Param("x") Long idParent);
}
