package com.che.jwttest.core.dao;

import com.che.jwttest.core.bo.tokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ItokenDao extends JpaRepository<tokenEntity,Long> {

	@Query("SELECT T FROM tokenEntity T INNER JOIN userEntity U ON T.user.idUser = U.idUser WHERE U.idUser = :userId AND (T.expired=FALSE OR T.revoked=FALSE)")
	Set<tokenEntity> findValidTokenByUser(Long userId);

	tokenEntity findByToken(String token);
}
