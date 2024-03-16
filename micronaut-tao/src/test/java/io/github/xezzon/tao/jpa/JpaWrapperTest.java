package io.github.xezzon.tao.jpa;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import io.github.xezzon.tao.jpa.user.GenderEnum;
import io.github.xezzon.tao.jpa.user.User;
import io.github.xezzon.tao.jpa.user.UserDAO;
import io.github.xezzon.tao.jpa.user.UserRepository;
import io.github.xezzon.tao.retrieval.CommonQuery;
import io.micronaut.data.model.Page;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * @author xezzon
 */
@MicronautTest
@TestInstance(Lifecycle.PER_CLASS)
class JpaWrapperTest {

  private static final List<User> DATASET = new ArrayList<>();

  @Inject
  protected UserDAO userDAO;
  @Inject
  protected UserRepository repository;

  @BeforeAll
  void beforeAll() {
    for (int i = 0; i < Byte.MAX_VALUE; i++) {
      User user = new User();
      user.setId(IdUtil.getSnowflakeNextIdStr());
      user.setName(RandomUtil.randomString(6));
      user.setAge(RandomUtil.randomInt(6, 60));
      user.setCredit(RandomUtil.randomBigDecimal(new BigDecimal("100.000")));
      user.setGender(RandomUtil.randomEle(GenderEnum.values()));
      user.setCreateTime(Instant.EPOCH.plus(
          RandomUtil.randomLong(365 * 30, 365 * 300),
          ChronoUnit.DAYS
      ));
      user.setDeleteDateTime(RandomUtil.randomBoolean() ?
          LocalDateTime.of(
              RandomUtil.randomInt(2000, 2999),
              RandomUtil.randomInt(1, 11),
              RandomUtil.randomInt(1, 27),
              RandomUtil.randomInt(1, 11),
              RandomUtil.randomInt(1, 59),
              RandomUtil.randomInt(1, 59)
          ) : null
      );
      user.setDeleted(user.getDeleteDateTime() != null);
      user.setDeleteDate(
          user.getDeleteDateTime() == null
              ? null
              : user.getDeleteDateTime().toLocalDate()
      );
      user.setDeleteTime(
          user.getDeleteDateTime() == null
              ? null
              : user.getDeleteDateTime().toLocalTime()
      );
      DATASET.add(user);
    }
    repository.saveAll(DATASET);
  }

  @Test
  void update() {
    User user = DATASET.parallelStream()
        .filter(o -> Objects.nonNull(o.getDeleteDateTime()))
        .filter(o -> !Objects.equals(o.getGender(), GenderEnum.UNKNOWN))
        .findAny().get();
    User newUser = new User();
    newUser.setId(user.getId());
    newUser.setName(RandomUtil.randomString(8));
    newUser.setGender(GenderEnum.UNKNOWN);
    newUser.setAge(100);
    newUser.setCredit(null);
    newUser.setDeleteDateTime(LocalDateTime.now());
    userDAO.update(newUser);
    User updatedUser = userDAO.get().findById(user.getId()).get();
    // 普通字段正常更新
    Assertions.assertEquals(
        newUser.getName(),
        updatedUser.getName()
    );
    Assertions.assertEquals(
        newUser.getAge(),
        updatedUser.getAge()
    );
    // 值为 NULL 的字段不更新
    Assertions.assertTrue(
        user.getCredit().subtract(updatedUser.getCredit()).abs()
            .compareTo(new BigDecimal("0.01")) < 1
    );
    Assertions.assertEquals(
        user.getDeleted(),
        updatedUser.getDeleted()
    );
    // updatable 属性为 false 的属性不更新
    Assertions.assertEquals(
        user.getGender(),
        updatedUser.getGender()
    );
  }

  /**
   * 正常分页、排序、筛选
   */
  @Test
  void query() {
    String condition = "(name LLIKE 'J' OR (age GT 18)) AND (gender IN 'MALE' OR deleteDateTime NULL true)";
    List<User> excepts = DATASET.parallelStream()
        .filter(user -> user.getName().startsWith("J") || user.getAge() > 18)
        .filter(user -> Objects.equals(GenderEnum.MALE, user.getGender())
            || user.getDeleteDateTime() == null)
        .sorted(Comparator.comparing(User::getCredit))
        .toList();
    int pageSize = 15;
    int pageNum = 1;

    CommonQuery commonQuery = new CommonQuery();
    commonQuery.setFilter(condition);
    commonQuery.setSort(Collections.singletonList("credit:ASC"));
    commonQuery.setPageSize(pageSize);
    commonQuery.setPageNum(pageNum);
    Page<User> users = userDAO.query(commonQuery);
    Assertions.assertEquals(excepts.size(), users.getTotalSize());
    Assertions.assertIterableEquals(
        excepts.parallelStream().limit(pageSize).toList(),
        users.getContent()
    );
  }

  /**
   * 无条件查询
   */
  @Test
  void query_unpaged() {
    Page<User> users = userDAO.query(new CommonQuery());
    Assertions.assertEquals(DATASET.size(), users.getTotalSize());
    Assertions.assertEquals(DATASET.size(), users.getContent().size());
  }

  /**
   * 字段找不到
   */
  @Test
  void query_unknown_field() {
    CommonQuery commonQuery = new CommonQuery();
    commonQuery.setFilter("field EQ 'value'");
    Assertions.assertThrowsExactly(
        UnsupportedOperationException.class,
        () -> userDAO.query(commonQuery)
    );
  }

  @Test
  void query_string() {
    CommonQuery eqQuery = new CommonQuery();
    String exceptName = DATASET.parallelStream()
        .map(User::getName)
        .findAny()
        .get();
    eqQuery.setFilter(String.format("name EQ '%s'", exceptName));
    Page<User> eqPage = userDAO.query(eqQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.equals(user.getName(), exceptName))
            .count(),
        eqPage.getTotalSize()
    );

    CommonQuery neQuery = new CommonQuery();
    neQuery.setFilter(String.format("name NE '%s'", exceptName));
    Page<User> nePage = userDAO.query(neQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> !Objects.equals(user.getName(), exceptName))
            .count(),
        nePage.getTotalSize()
    );
    Assertions.assertEquals(
        DATASET.size(),
        eqPage.getTotalSize() + nePage.getTotalSize()
    );

    CommonQuery llikeQuery = new CommonQuery();
    llikeQuery.setFilter(String.format("name LLIKE '%s'", exceptName.substring(0, 3)));
    Page<User> llikePage = userDAO.query(llikeQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> user.getName().startsWith(exceptName.substring(0, 3)))
            .count(),
        llikePage.getTotalSize()
    );

    Set<String> exceptId = DATASET.parallelStream()
        .limit(10)
        .map(User::getId)
        .collect(Collectors.toSet());
    CommonQuery inQuery = new CommonQuery();
    inQuery.setFilter(String.format("id IN '%s'", StrUtil.join(",", exceptId)));
    Page<User> inPage = userDAO.query(inQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> exceptId.contains(user.getId()))
            .count(),
        inPage.getTotalSize()
    );

    CommonQuery outQuery = new CommonQuery();
    outQuery.setFilter(String.format("id OUT '%s'", StrUtil.join(",", exceptId)));
    Page<User> outPage = userDAO.query(outQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> !exceptId.contains(user.getId()))
            .count(),
        outPage.getTotalSize()
    );
    Assertions.assertEquals(
        DATASET.size(),
        inPage.getTotalSize() + outPage.getTotalSize()
    );

    CommonQuery emptyInQuery1 = new CommonQuery();
    emptyInQuery1.setFilter("name IN ''");
    Page<User> emptyInPage1 = userDAO.query(emptyInQuery1);
    Assertions.assertEquals(
        0,
        emptyInPage1.getTotalSize()
    );

    CommonQuery emptyInQuery2 = new CommonQuery();
    emptyInQuery2.setFilter(String.format("name IN ',%s, ,'", exceptName));
    Page<User> emptyInPage2 = userDAO.query(emptyInQuery2);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.equals(user.getName(), exceptName))
            .count(),
        emptyInPage2.getTotalSize()
    );
  }

  @Test
  void query_enum() {
    CommonQuery inQuery = new CommonQuery();
    inQuery.setFilter("gender IN 'MALE,FEMALE'");
    Page<User> inPage = userDAO.query(inQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Set.of(GenderEnum.MALE, GenderEnum.FEMALE).contains(user.getGender()))
            .count(),
        inPage.getTotalSize()
    );

    CommonQuery outQuery = new CommonQuery();
    outQuery.setFilter("gender OUT 'MALE,FEMALE'");
    Page<User> outPage = userDAO.query(outQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> !Set.of(GenderEnum.MALE, GenderEnum.FEMALE).contains(user.getGender()))
            .count(),
        outPage.getTotalSize()
    );
    Assertions.assertEquals(
        DATASET.size(),
        inPage.getTotalSize() + outPage.getTotalSize()
    );

    CommonQuery allQuery = new CommonQuery();
    allQuery.setFilter("gender IN 'MALE,FEMALE,UNKNOWN'");
    Page<User> allPage = userDAO.query(allQuery);
    Assertions.assertEquals(
        DATASET.size(),
        allPage.getTotalSize()
    );

    CommonQuery emptyQuery = new CommonQuery();
    emptyQuery.setFilter("gender IN ''");
    Assertions.assertThrowsExactly(
        UnsupportedOperationException.class,
        () -> userDAO.query(emptyQuery)
    );
  }

  @Test
  void query_number() {
    String exceptNumber = "0.180e2";

    CommonQuery eqQuery = new CommonQuery();
    eqQuery.setFilter(String.format("age EQ %s", exceptNumber));
    Page<User> eqPage = userDAO.query(eqQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.equals(user.getAge(), 18))
            .count(),
        eqPage.getTotalSize()
    );

    CommonQuery neQuery = new CommonQuery();
    neQuery.setFilter(String.format("age NE %s", exceptNumber));
    Page<User> nePage = userDAO.query(neQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> !Objects.equals(user.getAge(), 18))
            .count(),
        nePage.getTotalSize()
    );
    Assertions.assertEquals(
        DATASET.size(),
        eqPage.getTotalSize() + nePage.getTotalSize()
    );

    CommonQuery gtQuery = new CommonQuery();
    gtQuery.setFilter(String.format("age GT %s", exceptNumber));
    Page<User> gtPage = userDAO.query(gtQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> user.getAge() > 18)
            .count(),
        gtPage.getTotalSize()
    );

    CommonQuery ltQuery = new CommonQuery();
    ltQuery.setFilter(String.format("age LT %s", exceptNumber));
    Page<User> ltPage = userDAO.query(ltQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> user.getAge() < 18)
            .count(),
        ltPage.getTotalSize()
    );

    CommonQuery geQuery = new CommonQuery();
    geQuery.setFilter(String.format("age GE %s", exceptNumber));
    Page<User> gePage = userDAO.query(geQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> user.getAge() >= 18)
            .count(),
        gePage.getTotalSize()
    );
    Assertions.assertEquals(
        DATASET.size(),
        gePage.getTotalSize() + ltPage.getTotalSize()
    );

    CommonQuery leQuery = new CommonQuery();
    leQuery.setFilter(String.format("age LE %s", exceptNumber));
    Page<User> lePage = userDAO.query(leQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> user.getAge() <= 18)
            .count(),
        lePage.getTotalSize()
    );
    Assertions.assertEquals(
        DATASET.size(),
        lePage.getTotalSize() + gtPage.getTotalSize()
    );
  }

  @Test
  void query_instant() {
    // yyyy-MM-ddTHH:mm:ss
    Instant exceptDateTime = DATASET.parallelStream()
        .findAny()
        .get()
        .getCreateTime();

    CommonQuery eqQuery = new CommonQuery();
    eqQuery.setFilter(String.format(
        "createTime EQ '%s'",
        exceptDateTime
    ));
    Page<User> eqPage = userDAO.query(eqQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.equals(user.getCreateTime(), exceptDateTime))
            .count(),
        eqPage.getTotalSize()
    );

    CommonQuery neQuery = new CommonQuery();
    neQuery.setFilter(String.format(
        "createTime NE '%s'",
        exceptDateTime
    ));
    Page<User> nePage = userDAO.query(neQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> !Objects.equals(user.getCreateTime(), exceptDateTime))
            .count(),
        nePage.getTotalSize()
    );

    CommonQuery gtQuery = new CommonQuery();
    gtQuery.setFilter(String.format(
        "createTime GT '%s'",
        exceptDateTime
    ));
    Page<User> gtPage = userDAO.query(gtQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> user.getCreateTime().isAfter(exceptDateTime))
            .count(),
        gtPage.getTotalSize()
    );

    CommonQuery ltQuery = new CommonQuery();
    ltQuery.setFilter(String.format(
        "createTime LT '%s'", exceptDateTime
    ));
    Page<User> ltPage = userDAO.query(ltQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> user.getCreateTime().isBefore(exceptDateTime))
            .count(),
        ltPage.getTotalSize()
    );

    CommonQuery geQuery = new CommonQuery();
    geQuery.setFilter(String.format(
        "createTime GE '%s'", exceptDateTime
    ));
    Page<User> gePage = userDAO.query(geQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> !user.getCreateTime().isBefore(exceptDateTime))
            .count(),
        gePage.getTotalSize()
    );

    CommonQuery leQuery = new CommonQuery();
    leQuery.setFilter(String.format(
        "createTime LE '%s'", exceptDateTime
    ));
    Page<User> lePage = userDAO.query(leQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> !user.getCreateTime().isAfter(exceptDateTime))
            .count(),
        lePage.getTotalSize()
    );
  }

  @Test
  void query_datetime() {
    // yyyy-MM-ddTHH:mm:ss
    LocalDateTime exceptDateTime = DATASET.parallelStream()
        .filter(user -> Objects.nonNull(user.getDeleteDateTime()))
        .findAny()
        .get().getDeleteDateTime();

    CommonQuery eqQuery = new CommonQuery();
    eqQuery.setFilter(String.format(
        "deleteDateTime NULL false AND deleteDateTime EQ '%s'",
        exceptDateTime
    ));
    Page<User> eqPage = userDAO.query(eqQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteDateTime()))
            .filter(user -> Objects.equals(user.getDeleteDateTime(), exceptDateTime))
            .count(),
        eqPage.getTotalSize()
    );

    CommonQuery neQuery = new CommonQuery();
    neQuery.setFilter(String.format(
        "deleteDateTime NULL false AND deleteDateTime NE '%s'",
        exceptDateTime
    ));
    Page<User> nePage = userDAO.query(neQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteDateTime()))
            .filter(user -> !Objects.equals(user.getDeleteDateTime(), exceptDateTime))
            .count(),
        nePage.getTotalSize()
    );

    CommonQuery gtQuery = new CommonQuery();
    gtQuery.setFilter(String.format(
        "deleteDateTime NULL false AND deleteDateTime GT '%s'",
        exceptDateTime
    ));
    Page<User> gtPage = userDAO.query(gtQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteDateTime()))
            .filter(user -> user.getDeleteDateTime().isAfter(exceptDateTime))
            .count(),
        gtPage.getTotalSize()
    );

    CommonQuery ltQuery = new CommonQuery();
    ltQuery.setFilter(String.format(
        "deleteDateTime NULL false AND deleteDateTime LT '%s'", exceptDateTime
    ));
    Page<User> ltPage = userDAO.query(ltQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteDateTime()))
            .filter(user -> user.getDeleteDateTime().isBefore(exceptDateTime))
            .count(),
        ltPage.getTotalSize()
    );

    CommonQuery geQuery = new CommonQuery();
    geQuery.setFilter(String.format(
        "deleteDateTime NULL false AND deleteDateTime GE '%s'", exceptDateTime
    ));
    Page<User> gePage = userDAO.query(geQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteDateTime()))
            .filter(user -> !user.getDeleteDateTime().isBefore(exceptDateTime))
            .count(),
        gePage.getTotalSize()
    );

    CommonQuery leQuery = new CommonQuery();
    leQuery.setFilter(String.format(
        "deleteDateTime NULL false AND deleteDateTime LE '%s'", exceptDateTime
    ));
    Page<User> lePage = userDAO.query(leQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteDateTime()))
            .filter(user -> !user.getDeleteDateTime().isAfter(exceptDateTime))
            .count(),
        lePage.getTotalSize()
    );
  }

  @Test
  void query_date() {
    // yyyy-MM-dd
    LocalDate exceptDate = DATASET.parallelStream()
        .filter(user -> Objects.nonNull(user.getDeleteDate()))
        .findAny()
        .get().getDeleteDate();

    CommonQuery eqQuery = new CommonQuery();
    eqQuery.setFilter(String.format(
        "deleteDate NULL false AND deleteDate EQ '%s'",
        exceptDate
    ));
    Page<User> eqPage = userDAO.query(eqQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteDate()))
            .filter(user -> Objects.equals(user.getDeleteDate(), exceptDate))
            .count(),
        eqPage.getTotalSize()
    );

    CommonQuery neQuery = new CommonQuery();
    neQuery.setFilter(String.format(
        "deleteDate NULL false AND deleteDate NE '%s'",
        exceptDate
    ));
    Page<User> nePage = userDAO.query(neQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteDate()))
            .filter(user -> !Objects.equals(user.getDeleteDate(), exceptDate))
            .count(),
        nePage.getTotalSize()
    );

    CommonQuery gtQuery = new CommonQuery();
    gtQuery.setFilter(String.format(
        "deleteDate NULL false AND deleteDate GT '%s'",
        exceptDate
    ));
    Page<User> gtPage = userDAO.query(gtQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteDate()))
            .filter(user -> user.getDeleteDate().isAfter(exceptDate))
            .count(),
        gtPage.getTotalSize()
    );

    CommonQuery ltQuery = new CommonQuery();
    ltQuery.setFilter(String.format(
        "deleteDate NULL false AND deleteDate LT '%s'", exceptDate
    ));
    Page<User> ltPage = userDAO.query(ltQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteDate()))
            .filter(user -> user.getDeleteDate().isBefore(exceptDate))
            .count(),
        ltPage.getTotalSize()
    );

    CommonQuery geQuery = new CommonQuery();
    geQuery.setFilter(String.format(
        "deleteDate NULL false AND deleteDate GE '%s'", exceptDate
    ));
    Page<User> gePage = userDAO.query(geQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteDate()))
            .filter(user -> !user.getDeleteDate().isBefore(exceptDate))
            .count(),
        gePage.getTotalSize()
    );

    CommonQuery leQuery = new CommonQuery();
    leQuery.setFilter(String.format(
        "deleteDate NULL false AND deleteDate LE '%s'", exceptDate
    ));
    Page<User> lePage = userDAO.query(leQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteDate()))
            .filter(user -> !user.getDeleteDate().isAfter(exceptDate))
            .count(),
        lePage.getTotalSize()
    );
  }

  @Test
  void query_time() {
    // HH:mm:ss
    LocalTime exceptTime = DATASET.parallelStream()
        .filter(user -> Objects.nonNull(user.getDeleteTime()))
        .findAny()
        .get().getDeleteTime();

    CommonQuery eqQuery = new CommonQuery();
    eqQuery.setFilter(String.format(
        "deleteTime NULL false AND deleteTime EQ '%s'",
        exceptTime
    ));
    Page<User> eqPage = userDAO.query(eqQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteTime()))
            .filter(user -> Objects.equals(user.getDeleteTime(), exceptTime))
            .count(),
        eqPage.getTotalSize()
    );

    CommonQuery neQuery = new CommonQuery();
    neQuery.setFilter(String.format(
        "deleteTime NULL false AND deleteTime NE '%s'",
        exceptTime
    ));
    Page<User> nePage = userDAO.query(neQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteTime()))
            .filter(user -> !Objects.equals(user.getDeleteTime(), exceptTime))
            .count(),
        nePage.getTotalSize()
    );

    CommonQuery gtQuery = new CommonQuery();
    gtQuery.setFilter(String.format(
        "deleteTime NULL false AND deleteTime GT '%s'",
        exceptTime
    ));
    Page<User> gtPage = userDAO.query(gtQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteTime()))
            .filter(user -> user.getDeleteTime().isAfter(exceptTime))
            .count(),
        gtPage.getTotalSize()
    );

    CommonQuery ltQuery = new CommonQuery();
    ltQuery.setFilter(String.format(
        "deleteTime NULL false AND deleteTime LT '%s'", exceptTime
    ));
    Page<User> ltPage = userDAO.query(ltQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteTime()))
            .filter(user -> user.getDeleteTime().isBefore(exceptTime))
            .count(),
        ltPage.getTotalSize()
    );

    CommonQuery geQuery = new CommonQuery();
    geQuery.setFilter(String.format(
        "deleteTime NULL false AND deleteTime GE '%s'", exceptTime
    ));
    Page<User> gePage = userDAO.query(geQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteTime()))
            .filter(user -> !user.getDeleteTime().isBefore(exceptTime))
            .count(),
        gePage.getTotalSize()
    );

    CommonQuery leQuery = new CommonQuery();
    leQuery.setFilter(String.format(
        "deleteTime NULL false AND deleteTime LE '%s'", exceptTime
    ));
    Page<User> lePage = userDAO.query(leQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.nonNull(user.getDeleteTime()))
            .filter(user -> !user.getDeleteTime().isAfter(exceptTime))
            .count(),
        lePage.getTotalSize()
    );
  }

  @Test
  void query_boolean() {
    CommonQuery eqQuery = new CommonQuery();
    eqQuery.setFilter("deleted EQ true");
    Page<User> eqPage = userDAO.query(eqQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> Objects.equals(user.getDeleted(), true))
            .count(),
        eqPage.getTotalSize()
    );

    CommonQuery neQuery = new CommonQuery();
    neQuery.setFilter("deleted NE false");
    Page<User> nePage = userDAO.query(neQuery);
    Assertions.assertEquals(
        DATASET.parallelStream()
            .filter(user -> !Objects.equals(user.getDeleted(), false))
            .count(),
        nePage.getTotalSize()
    );
    Assertions.assertEquals(
        eqPage.getTotalSize(),
        nePage.getTotalSize()
    );
  }
}

