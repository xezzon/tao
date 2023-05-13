package io.github.xezzon.tao.util;

import cn.hutool.core.util.StrUtil;
import io.github.xezzon.tao.desensitize.Desensitizer;

public class DesensitizedUtil extends cn.hutool.core.util.DesensitizedUtil {

  public static String desensitized(CharSequence str, Desensitizer desensitizer) {
    if (StrUtil.isBlank(str)) {
      return "";
    }
    return desensitizer.desensitize(String.valueOf(str));
  }

  public static class FirstMaskDesensitizer implements Desensitizer {

    @Override
    public String desensitize(String origin) {
      return firstMask(origin);
    }
  }

  public static class UserIdDesensitizer implements Desensitizer {

    @Override
    public String desensitize(String origin) {
      return String.valueOf(userId());
    }
  }

  public static class ChineseNameDesensitizer implements Desensitizer {

    @Override
    public String desensitize(String origin) {
      return chineseName(origin);
    }
  }

  public static class IdCardDesensitizer implements Desensitizer {

    @Override
    public String desensitize(String origin) {
      return idCardNum(origin, 1, 2);
    }
  }

  public static class FixedPhoneDesensitizer implements Desensitizer {

    @Override
    public String desensitize(String origin) {
      return fixedPhone(origin);
    }
  }

  public static class MobilePhoneDesensitizer implements Desensitizer {

    @Override
    public String desensitize(String origin) {
      return mobilePhone(origin);
    }
  }

  public static class AddressDesensitizer implements Desensitizer {

    @Override
    public String desensitize(String origin) {
      return address(origin, 8);
    }
  }

  public static class EmailDesensitizer implements Desensitizer {

    @Override
    public String desensitize(String origin) {
      return email(origin);
    }
  }

  public static class PasswordDesensitizer implements Desensitizer {

    @Override
    public String desensitize(String origin) {
      return password(origin);
    }
  }

  public static class CarLicenseDesensitizer implements Desensitizer {

    @Override
    public String desensitize(String origin) {
      return carLicense(origin);
    }
  }

  public static class BankCardDesensitizer implements Desensitizer {

    @Override
    public String desensitize(String origin) {
      return bankCard(origin);
    }
  }

  public static class Ipv4Desensitizer implements Desensitizer {

    @Override
    public String desensitize(String origin) {
      return ipv4(origin);
    }
  }

  public static class Ipv6Desensitizer implements Desensitizer {

    @Override
    public String desensitize(String origin) {
      return ipv6(origin);
    }
  }
}
