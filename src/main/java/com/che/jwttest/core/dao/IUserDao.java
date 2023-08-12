package com.che.jwttest.core.dao;

import com.che.jwttest.core.bo.Role;
import com.che.jwttest.core.bo.userEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserDao extends JpaRepository<userEntity,Long> {
	userEntity findByEmail(String email);
	userEntity findByRole(Role role);
}
