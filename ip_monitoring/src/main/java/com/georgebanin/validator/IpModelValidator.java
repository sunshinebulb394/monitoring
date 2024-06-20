package com.georgebanin.validator;

import com.georgebanin.dto.IpModelDto;
import com.georgebanin.utils.PingUtilitiez;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import static com.georgebanin.utils.PingUtilitiez.checkIfIpIsValidIp;

@Getter
@Setter
@Slf4j
public class IpModelValidator implements ConstraintValidator<CustomValidation, IpModelDto> {


    @Override
    public void initialize(CustomValidation constraintAnnotation) {
    }

    @Override
    public boolean isValid(IpModelDto value, ConstraintValidatorContext context) {
        try {

            if(value.ipAddress() == null || value.ipAddress().isEmpty()){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Ip Address is null or empty").addConstraintViolation();
                return false;
            }


            var isIpValid = checkIfIpIsValidIp(value.ipAddress());
            if(!isIpValid){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Ip Address is not a valid IP(IpV4 or IpV6)").addConstraintViolation();
                return false;
            }

            return true;
        }catch (UnknownHostException e){
            throw new RuntimeException("Ip address is not valid");
        }

    }



}
