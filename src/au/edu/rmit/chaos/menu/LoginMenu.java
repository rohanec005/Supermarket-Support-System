package au.edu.rmit.chaos.menu;

import java.util.Scanner;

import au.edu.rmit.chaos.Company;
import au.edu.rmit.chaos.Customer;
import au.edu.rmit.chaos.Employee;

public class LoginMenu {
    private static Scanner scan;
    private static String username;
    private static String password;

    public static Customer displayCustomerLogin() {

        scan = new Scanner(System.in);
        System.out.println("\n\tHello Customer, Please enter your login details to continue");
        System.out.print("\tUsername: ");
        username = scan.nextLine();
        System.out.print("\tPassword: ");
        password = scan.nextLine();

        System.out.println("\n\tPlease Wait...");
        Customer cu = new Customer(username, password, true);

        System.out.println("\n\tPress any key to continue...");
        scan.nextLine();
        return cu;
    }

    public static Employee displayEmployeeLogin() {
        scan = new Scanner(System.in);
        System.out.println("\n\tHello Employee, Please enter your login details to continue");
        System.out.print("\tUsername: ");
        username = scan.nextLine();
        System.out.print("\tPassword: ");
        password = scan.nextLine();

        System.out.println("\n\tPlease Wait...");
        Employee emp = new Employee(username, password);

        System.out.println("\n\tPress any key to continue...");
        scan.nextLine();

        return emp;
    }

}
