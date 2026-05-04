package edu.upes.lostfound.util;

public class PasswordHashTool {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java edu.upes.lostfound.util.PasswordHashTool <password>");
            return;
        }
        for (String password : args) {
            System.out.println(password + "=" + PasswordUtil.hashPassword(password));
        }
    }
}
