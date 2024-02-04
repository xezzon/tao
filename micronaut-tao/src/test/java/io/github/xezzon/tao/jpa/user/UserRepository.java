package io.github.xezzon.tao.jpa.user;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

/**
 * @author xezzon
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
