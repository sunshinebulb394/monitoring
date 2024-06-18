package com.georgebanin.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.UnknownHostException;
import java.util.regex.Pattern;

@Slf4j
public class PingUtilitiez {
    private static   final String IPV4_REGEX =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private static   final String IPV6_REGEX =
            "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";

    private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);
    private static final Pattern IPV6_PATTERN = Pattern.compile(IPV6_REGEX);

    /**
     * Check if ip is a valid ipv4 or ipv6
     * @param ip
     * @return if ip is valid or not
     * @throws UnknownHostException
     */
    public static boolean checkIfIpIsValidIp(String ip) throws UnknownHostException {
        log.info("Checking if ip is valid");
        if(IPV4_PATTERN.matcher(ip).matches()){
            return true;
        }
        return IPV6_PATTERN.matcher(ip).matches();
    }
}
