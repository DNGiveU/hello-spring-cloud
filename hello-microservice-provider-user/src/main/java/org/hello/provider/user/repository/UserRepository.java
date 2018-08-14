package org.hello.provider.user.repository;

import org.hello.provider.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author gaz
 * @date 2018年8月14日
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
