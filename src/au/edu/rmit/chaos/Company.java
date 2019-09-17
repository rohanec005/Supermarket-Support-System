package au.edu.rmit.chaos;

import java.util.*;

import au.edu.rmit.chaos.menu.WarehouseMenu;

import au.edu.rmit.chaos.menu.CustomerMenu;
import au.edu.rmit.chaos.menu.LoginMenu;
import au.edu.rmit.chaos.menu.ManagerMenu;
import au.edu.rmit.chaos.menu.SalesStaffMenu;
import au.edu.rmit.chaos.report.Report;

import javax.sound.midi.Soundbank;

public class Company {

    public Customer customer;
    public Employee employee;
    private CustomerMenu cuMenu;
    private SalesStaffMenu sMenu;
    private WarehouseMenu wMenu;
    private ManagerMenu mMenu;


    private static Scanner scan;


    private static char displayMainMenu() throws Exception {
        System.out.println("\n\n\t\tSupermarket Support System\n");
        System.out.println("\tCustomer				0");
        System.out.println("\tEmployee				1");
        System.out.println("\tExit					3");
        System.out.println("\n\t*****************************************");
        System.out.print("\tYour choice : ");
        scan = new Scanner(System.in);
        return scan.nextLine().charAt(0);
    }

    public static void main(String args[]) {
        Company comp = new Company();
        char c = 0;
        do {

            try {
                c = displayMainMenu();
            } catch (Exception e) {
                System.out.println("invalid option!");
            }
            if (c == '0') { // customer menu
                comp.customer = LoginMenu.displayCustomerLogin();
                if (comp.customer.isLoggedIn()) {
                    comp.cuMenu = new CustomerMenu(comp.customer);
                    comp.cuMenu.display();
                }
            } else if (c == '1') { // employee menu
                comp.employee = LoginMenu.displayEmployeeLogin();
                if (comp.employee.isLoggedIn()) {
                    if (comp.employee.getRole() == EmployeeRole.sales) {
                        comp.sMenu = new SalesStaffMenu(comp.employee);
                        comp.sMenu.display();
                    } else if (comp.employee.getRole() == EmployeeRole.warehouse) {
                        comp.wMenu = new WarehouseMenu(comp.employee);
                        comp.wMenu.display();
                    } else if (comp.employee.getRole() == EmployeeRole.manager) {
                        comp.mMenu = new ManagerMenu(comp.employee);
                        comp.mMenu.display();
                    }
                }
            }
        } while (c != '3');
    }
}
