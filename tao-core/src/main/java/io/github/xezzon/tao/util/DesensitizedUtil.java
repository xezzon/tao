package io.github.xezzon.tao.util;

import cn.hutool.core.util.ReflectUtil;
import io.github.xezzon.tao.desensitize.Desensitizer;

public class DesensitizedUtil extends cn.hutool.core.util.DesensitizedUtil {

  public static String desensitized(CharSequence str, Desensitizer desensitizer) {
    if (str == null || String.valueOf(str).isBlank()) {
      return "";
    }
    return desensitizer.desensitize(String.valueOf(str));
  }

  public static String desensitized(CharSequence str, Class<? extends Desensitizer> clazz) {
    return desensitized(str, ReflectUtil.newInstance(clazz));
  }

  public static class FirstMaskDesensitizer implements Desensitizer {

    public static final FirstMaskDesensitizer INSTANCE = new FirstMaskDesensitizer();

    @Override
    public String desensitize(String origin) {
      return firstMask(origin);
    }
  }

  public static class UserIdDesensitizer implements Desensitizer {

    public static final UserIdDesensitizer INSTANCE = new UserIdDesensitizer();

    @Override
    public String desensitize(String origin) {
      return String.valueOf(userId());
    }
  }

  public static class ChineseNameDesensitizer implements Desensitizer {

    public static final ChineseNameDesensitizer INSTANCE = new ChineseNameDesensitizer();

    @Override
    public String desensitize(String origin) {
      return chineseName(origin);
    }
  }

  public static class IdCardDesensitizer implements Desensitizer {

    public static final IdCardDesensitizer INSTANCE = new IdCardDesensitizer();

    @Override
    public String desensitize(String origin) {
      return idCardNum(origin, 1, 2);
    }
  }

  public static class FixedPhoneDesensitizer implements Desensitizer {

    public static final FixedPhoneDesensitizer INSTANCE = new FixedPhoneDesensitizer();

    @Override
    public String desensitize(String origin) {
      return fixedPhone(origin);
    }
  }

  public static class MobilePhoneDesensitizer implements Desensitizer {

    public static final MobilePhoneDesensitizer INSTANCE = new MobilePhoneDesensitizer();

    @Override
    public String desensitize(String origin) {
      return mobilePhone(origin);
    }
  }

  public static class AddressDesensitizer implements Desensitizer {

    public static final AddressDesensitizer INSTANCE = new AddressDesensitizer();

    @Override
    public String desensitize(String origin) {
      return address(origin, 8);
    }
  }

  public static class EmailDesensitizer implements Desensitizer {

    public static final EmailDesensitizer INSTANCE = new EmailDesensitizer();

    @Override
    public String desensitize(String origin) {
      return email(origin);
    }
  }

  public static class PasswordDesensitizer implements Desensitizer {

    public static final PasswordDesensitizer INSTANCE = new PasswordDesensitizer();

    @Override
    public String desensitize(String origin) {
      return password(origin);
    }
  }

  public static class CarLicenseDesensitizer implements Desensitizer {

    public static final CarLicenseDesensitizer INSTANCE = new CarLicenseDesensitizer();

    @Override
    public String desensitize(String origin) {
      return carLicense(origin);
    }
  }

  public static class BankCardDesensitizer implements Desensitizer {

    public static final BankCardDesensitizer INSTANCE = new BankCardDesensitizer();

    @Override
    public String desensitize(String origin) {
      return bankCard(origin);
    }
  }

  public static class Ipv4Desensitizer implements Desensitizer {

    public static final Ipv4Desensitizer INSTANCE = new Ipv4Desensitizer();

    @Override
    public String desensitize(String origin) {
      return ipv4(origin);
    }
  }

  public static class Ipv6Desensitizer implements Desensitizer {

    public static final Ipv6Desensitizer INSTANCE = new Ipv6Desensitizer();

    @Override
    public String desensitize(String origin) {
      return ipv6(origin);
    }
  }
}
