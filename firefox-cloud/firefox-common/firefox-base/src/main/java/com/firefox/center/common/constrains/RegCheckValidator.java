package com.firefox.center.common.constrains;

import com.firefox.center.common.annotation.RegCheck;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 正则格式校验
 * @Author sujie
 **/

public class RegCheckValidator implements ConstraintValidator<RegCheck, Object> {

  private String regExpre;

  @Override
  public void initialize(RegCheck dateCheck) {
    this.regExpre = dateCheck.value();
  }

  @Override
  public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
    if (null == object) {
      return true;
    }
    String res = object.toString();
    if ("".equals(res)) {
      return true;
    } else {
      return res.matches(regExpre);
    }
  }
}
