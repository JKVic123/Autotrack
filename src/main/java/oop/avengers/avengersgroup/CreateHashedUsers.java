package oop.avengers.avengersgroup;

public class CreateHashedUsers {

    public static void main(String[] args) {
        String adminPass = "admin";
        String cashierPass = "cashier";

        String hashedAdminPass = PasswordUtils.hashPassword(adminPass);
        String hashedCashierPass = PasswordUtils.hashPassword(cashierPass);

        System.out.println("--- COPY-PASTE THE LINE BELOW INTO MONGODB COMPASS ---");
        System.out.println("{ \"username\": \"admin\", \"password\": \"" + hashedAdminPass + "\", \"role\": \"Admin\" }");

        System.out.println("\n--- COPY-PASTE THIS LINE NEXT ---");
        System.out.println("{ \"username\": \"cashier\", \"password\": \"" + hashedCashierPass + "\", \"role\": \"Cashier\" }");
    }
}
