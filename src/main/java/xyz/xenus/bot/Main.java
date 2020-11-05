package xyz.xenus.bot;

import xyz.xenus.lib.client.XenClient;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException {
        new XenClient().build();
    }
}
