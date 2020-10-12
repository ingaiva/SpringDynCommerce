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
	 	
}
