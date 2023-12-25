package com.pigeoff.menu.util;

public class Registre {
    private final boolean activate;
    public Registre(boolean activate) {
        this.activate = activate;
    }

    public void log(String message) {
        if (!activate) return;
        System.out.println(message);
    }

    public void log(String tag, String message) {
        if (!activate) return;
        System.out.printf("%s : %s\n", tag, message);
    }
}
