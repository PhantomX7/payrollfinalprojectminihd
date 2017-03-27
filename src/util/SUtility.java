/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author Phantom
 */
public class SUtility {

    public static void msg(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, " App Info", 1);
    }

    public static void mse(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, " App Info", 2);
    }

    public static int msq(Component parent, String message) {
        int respon = JOptionPane.showOptionDialog(parent, message, "App Info", JOptionPane.YES_NO_OPTION, 3, null, null, null);
        return respon;
    }

    public static void msg(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, 1);
    }

    public static void mse(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, 2);
    }

    public static int msq(Component parent, String message, String title) {
        int respon = JOptionPane.showOptionDialog(parent, message, title, JOptionPane.YES_NO_OPTION, 3, null, null, null);
        return respon;
    }

}
