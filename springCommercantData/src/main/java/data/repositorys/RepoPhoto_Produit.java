package data.repositorys;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import data.entitys.Photo_Produit;

@CrossOrigin("*")
@RepositoryRestResource
public interface RepoPhoto_Produit extends JpaRepository<Photo_Produit, Long>  {
	public static  Pageable firstPage= PageRequest.of(0, 10);

	
	@Query("select distinct ph from Photo_Produit ph where ph.produit.id =:x ORDER BY ph.ordre" )
	public List<Photo_Produit> getByProduit(@Param("x") Long id);
	
	@Query(value = "SELECT ph.ordre FROM Photo_Produit ph where ph.id =:x")
	public Integer getOrdre(@Param("x") Long id);
	
	@Query(value = "SELECT ph.ordre FROM Photo_Produit ph where ph.id !=:x and ph.ordre > :startOrdre ORDER BY ph.ordre" )
	public List<Integer> getNextOrdre(@Param("x") Long id,@Param("startOrdre") Integer startOrdre , Pageable pageable);
	
	@Query(value = "SELECT ph.ordre FROM Photo_Produit ph where ph.id !=:x and ph.ordre < :startOrdre ORDER BY ph.ordre DESC" )
	public List<Integer> getPrevOrdre(@Param("x") Long id,@Param("startOrdre") Integer startOrdre , Pageable pageable);
	

	@Query(value = "SELECT max(ph.ordre) FROM Photo_Produit ph where ph.produit.id =:x")
	public Integer maxOrdre(@Param("x") Long id);
	
	@Transactional
	@Modifying
	@Query("update Photo_Produit ph set  ph.ordre=:newOrdre where ph.id != :id and ph.ordre=:oldOrdre")
	int updatePhotoOrder( @Param("id") Long idToExclude,  @Param("newOrdre") Integer newOrdre,  @Param("oldOrdre") Integer oldOrdre);
	
	
	@Transactional
	@Modifying
	@Query("update Photo_Produit ph set ph.legende = :legende, ph.ordre=:ordre where ph.id = :id")
	int updatePhotoInfo( @Param("id") Long id, @Param("legende") String legende, @Param("ordre") Integer ordre);
	
	@Transactional
	@Modifying
	@Query("delete from Photo_Produit ph where ph.id=:x")
	public void deleteById(@Param("x") Long id);
	
	@Transactional
	@Modifying
	@Query("delete from Photo_Produit ph where ph.produit.id=:x")
	public void deleteByProduit(@Param("x") Long id);
}
