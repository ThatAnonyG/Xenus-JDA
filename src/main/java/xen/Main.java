package xen;

import xen.lib.client.XenClient;

import javax.security.auth.login.LoginException;

public class Main {
  public static void main(String[] args) throws LoginException, NoSuchFieldException {
    new XenClient().build();
  }
}
