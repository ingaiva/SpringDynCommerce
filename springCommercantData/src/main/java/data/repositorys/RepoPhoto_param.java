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

import data.entitys.Photo_param;

@CrossOrigin("*")
@RepositoryRestResource
public interface RepoPhoto_param extends JpaRepository<Photo_param, Long> {
	public static Pageable firstPage = PageRequest.of(0, 10);

	@Query("select distinct ph from Photo_param ph where ph.param.id =:x ORDER BY ph.ordre")
	public List<Photo_param> getPhotos(@Param("x") Long id);

	@Query(value = "SELECT ph.ordre FROM Photo_param ph where ph.id =:x")
	public Integer getOrdre(@Param("x") Long id);

	@Query(value = "SELECT ph.ordre FROM Photo_param ph where ph.id !=:x and ph.ordre > :startOrdre ORDER BY ph.ordre")
	public List<Integer> getNextOrdre(@Param("x") Long id, @Param("startOrdre") Integer startOrdre, Pageable pageable);

	@Query(value = "SELECT ph.ordre FROM Photo_param ph where ph.id !=:x and ph.ordre < :startOrdre ORDER BY ph.ordre DESC")
	public List<Integer> getPrevOrdre(@Param("x") Long id, @Param("startOrdre") Integer startOrdre, Pageable pageable);
	
	@Query(value = "SELECT max(ph.ordre) FROM Photo_param ph where ph.param.id =:x")
	public Integer maxOrdre(@Param("x") Long id);

	@Transactional
	@Modifying
	@Query("update Photo_param ph set  ph.ordre=:newOrdre where ph.id != :id and ph.ordre=:oldOrdre")
	int updatePhotoOrder(@Param("id") Long idToExclude, @Param("newOrdre") Integer newOrdre,
			@Param("oldOrdre") Integer oldOrdre);

	
	@Transactional
	@Modifying
	@Query("delete from Photo_param ph where ph.id=:x")
	public void deleteById(@Param("x") Long id);

	@Transactional
	@Modifying
	@Query("delete from Photo_param ph where ph.param.id=:x")
	public void deleteByParam(@Param("x") Long id);
}
