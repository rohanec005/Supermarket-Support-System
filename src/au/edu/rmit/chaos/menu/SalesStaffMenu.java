package au.edu.rmit.chaos.menu;

import java.util.InputMismatchException;
import java.util.Scanner;

import au.edu.rmit.chaos.*;

public class SalesStaffMenu extends Menu {
    private Employee employee;
    private Scanner scan;

    public SalesStaffMenu(Employee emp) {
        this.employee = emp;
        scan = new Scanner(System.in);
    }

    public void cancelOrderMenu() {
        int or = displayIntegerInputMessage("Please enter order number");
        if (or == -1) {
            invalidOptionMessage();
            return;
        }
        if (Order.cancelOrder(or)) {
            System.out.print("\n\tOrder canceled successfully 😃");
        } else {
            System.out.print("\n\tOrder canceled successfully 😃");
        }
        pressToContinue();
    }

    public void topUpCustomer() {
        String customerUser;
        customerUser = displayStringInputMessage("Please enter customer's username");
        Customer cu = Customer.searchByUsername(customerUser);
        if (cu == null) {
            pressToContinue();
            return;
        }
        double amount = displayDoubleInputMessage("Enter the amount to topup " + cu.getName() + "'s card");
        if (amount == 0) {
            invalidOptionMessage();
            pressToContinue();
            return;
        }
        if (!cu.increaseBalance(amount)) {
            pressToContinue();
            return;
        }

        System.out.print(
                "\t" + cu.getName() + "'s card has being topped up successfully\n\tNew Balance is " + cu.getBalance());
        pressToContinue();
        return;
    }

    public void editOrderMenu() {
        Order or = new Order(null);

        int orID = displayIntegerInputMessage("Please enter order number");
        if (orID <= 0) {
            invalidOptionMessage();
            pressToContinue();
            return;
        }

        if (!or.getOrderByID(orID)) {
            pressToContinue();
            return;
        }

        String title = String.format("%-40s", "Edit order #" + or.getID());
        int prID = displayOrderItemsInput(or, title, true);
        if (prID < 0) {
            invalidOptionMessage();
            pressToContinue();
            return;
        } else if (prID == or.getOrderItems().size()) {
            return;
        }

        if (!or.removeProduct(or.getOrderItems().get(prID))) {
            pressToContinue();
            return;
        }

        System.out.print("\n\tItem removed successfully 😃");
        pressToContinue();
        return;

    }

    public void display() {
        int option = 0;
        do {
            System.out.println("\n\n\t\t\tWelcome " + employee.getName());
            System.out.printf("\t %-50s %-2s %n", "Cancel Order", "0");
            System.out.printf("\t %-50s %-2s %n", "Edit Order", "1");
            System.out.printf("\t %-50s %-2s %n", "Topup Customer's card", "2");
            System.out.printf("\t %-50s %-2s %n", "Exit", "3");
            System.out.println(String.format("%n\t%-53s%n", " ").replace(" ", "*"));
            System.out.print("\tYour choice : ");

            try {
                option = scan.nextInt();
                scan.nextLine();
            } catch (Exception e) {

            }
            if (option == 0) {
                cancelOrderMenu();
            } else if (option == 1) {
                editOrderMenu();
            } else if (option == 2) {
                topUpCustomer();
            } else if (option == 3) {
                System.exit(0);
            }

        } while (true);

    }
}
