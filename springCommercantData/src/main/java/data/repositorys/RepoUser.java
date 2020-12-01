package data.repositorys;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import data.entitys.*;
public interface RepoUser   extends JpaRepository<User, Long> {

	@Query("select u from User u where u.mail =:x")
	public Set<User> getUserByMail(@Param("x") String mail);
	
	@Query("select u from User u where u.mail =:x and u.id !=:y")
	public Set<User> getUserByMail(@Param("x") String mail,@Param("y") Long idUserToExclude);
	
	@Query("select u, count(cmd.id) as nbCmd , SUM(cmd.totalFinal) as totalCmd from User u LEFT JOIN u.commandes cmd where cmd.statut in (:y) GROUP BY u.id")
	public List getUsersStatisticsFiltered(@Param("y") List<String> statutValues);
	
	@Query("select count(*) from Commande cmd where cmd.user.id=:x and cmd.statut in (:y)")
	public Integer getCountByUserFiltered(@Param("x") Long idUser, @Param("y") List<String> statutValues);
	
	@Query("select  DISTINCT u from User u join u.pointsVente p where p.id in (:x)")
	List<User> findAllByListPointsVente(@Param("x") List<Long> listIdPointsVente);

	List<User> findAllByPointsVente(@Param("x") PointVente pt);
}
