package indi.xezzon.tao.user;

/**
 * 用户信息
 * @author xezzon
 */
public class UserContext {
  private static final InheritableThreadLocal<String> CURRENT_USER_ID = new InheritableThreadLocal<>();

  public static String getId() {
    return CURRENT_USER_ID.get();
  }

  public static void setUser(String id) {
    if (id != null) {
      CURRENT_USER_ID.set(id);
    } else {
      CURRENT_USER_ID.remove();
    }
  }
}
