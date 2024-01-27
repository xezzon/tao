package io.github.xezzon.tao.jpa.user;

import io.github.xezzon.tao.jpa.BaseJpaWrapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO extends BaseJpaWrapper<User, QUser, UserRepository> {


  protected UserDAO(UserRepository dao) {
    super(dao);
  }

  @Override
  protected QUser getQuery() {
    return QUser.user;
  }

  @Override
  protected Class<User> getBeanClass() {
    return User.class;
  }
}
