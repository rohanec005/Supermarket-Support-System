package au.edu.rmit.chaos.menu;

import java.util.ArrayList;
import java.util.Scanner;

import au.edu.rmit.chaos.*;

public class Menu {

    private Scanner scan = new Scanner(System.in);

    protected void pressToContinue() {
        System.out.println("\n\n\tPress return to continue...");
        scan.nextLine();
    }

    protected int displayOrderItemsInput(Order or, String title, boolean back) {
        System.out.println("\t" + title);
        int prID;
        for (int x = 0; x < or.getOrderItems().size(); x++) {
            OrderItem item = or.getOrderItems().get(x);
            System.out.printf("\t%-40s %-10s %n", item.getProduct().getName(), x);
        }
        System.out.print("\n\tPlease enter item number to remove it: ");
        try {
            prID = scan.nextInt();
        } catch (Exception ex) {
            return -1;
        }
        return prID;
    }

    protected void invalidOptionMessage() {
        System.out.println("\tPlease enter a valid number 😒");
    }

    protected Discount displayDiscountInput(ArrayList<Product> products) {
        System.out.println("\n\n\t\t\tAvailable Discounts");
        ArrayList<Discount> discounts = new ArrayList<Discount>();
        int counter = 0;
        // loop through products
        for (Product pr : products) {
            // loop through discounts
            for (Discount disc : pr.getDiscounts()) {
                String txt = pr.getName() + " " + disc.getPercentage() + "% discount on " + disc.getQuantity() + pr.getType();
                System.out.printf("\t" + "%-60s %-10s %n", txt, counter);
                discounts.add(disc);
                counter++;
            }
        }
        System.out.printf("\t" + "%-60s %d %n", "Back to main menu", counter);
        System.out.println("\n\t" + String.format("%60s", "*").replace(' ', '*'));
        int option = displayIntegerInputMessage("Your choice");
        if (option < 0 || option == counter || option > counter)
            return null;
        return discounts.get(option);
    }

    protected void displayDiscount(ArrayList<Product> products) {
        boolean discountsAvailable = false;
        System.out.println("\n\n\t\t\tAvailable Discounts");
        System.out.printf("\t" + "%-30s %-10s %s %n", "Product", "qty", "percentage");
        // loop through products
        for (Product pr : products) {
            // loop through discounts
            for (Discount disc : pr.getDiscounts()) {
                System.out.printf("\t" + "%-30s %-10s %s%% %n", pr.getName(), disc.getQuantity(), disc.getPercentage());
                discountsAvailable = true;
            }
        }
        if (!discountsAvailable) {
            System.out.println("\n\t\tNo Available Disounts\n");
        }
    }

    protected double displayDoubleInputMessage(String message) {
        double option;
        System.out.print("\t" + message + ": ");
        try {
            option = scan.nextDouble();
            scan.nextLine();
        } catch (Exception e) {
            return -1;
        }
        return option;
    }

    protected boolean displayYesOrNoMessage(String title) {
        char ch;
        do {
            System.out.print("\t" + title + " Y/N: ");
            ch = scan.nextLine().trim().charAt(0);
        } while (ch != 'y' && ch != 'n');
        return ch == 'y' ? true : false;
    }

    protected int displayIntegerInputMessage(String message) {
        int option;
        System.out.print("\t" + message + ": ");
        try {
            option = scan.nextInt();
            scan.nextLine();
        } catch (Exception e) {
            return -1;
        }
        return option;
    }

    protected String displayDateInputMessage(String message) {
        String date;
        System.out.print("\t" + message + " dd-mm-yyyy: ");
        try {
            date = scan.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return date;
    }


    protected String displayStringInputMessage(String message) {
        String option;
        System.out.print("\t" + message + ": ");
        try {
            option = scan.nextLine();
        } catch (Exception e) {
            return null;
        }
        return option.trim();
    }

    protected int displaySuppliersInput(ArrayList<Supplier> suppliers, String title, boolean back) {
        int option;
        System.out.println("\n\n\t\t" + title);
        for (int x = 0; x < suppliers.size(); x++) {
            System.out.printf("\t" + "%-40s %d %n", suppliers.get(x).getName(), x);
        }

        if (back)
            System.out.printf("\t" + "%-40s %d %n", "Back to main menu", suppliers.size());

        System.out.println("\n\t" + String.format("%40s", "*").replace(' ', '*'));
        System.out.print("\tYour choice : ");

        try {
            option = scan.nextInt();
            scan.nextLine();
        } catch (Exception e) {
            return -1;
        }

        // exit selected
        if (back) {
            if (option == suppliers.size()) {
                return suppliers.size();
            }

            // unknown selection
            if (option > suppliers.size()) {
                return -1;
            }
        } else {
            // unknown selection
            if (option > suppliers.size() - 1) {
                return -1;
            }
        }
        return option;
    }

    protected int displayProductsInput(ArrayList<Product> products, String title, boolean back) {
        int option;
        System.out.println("\n\n\t\t" + title);
        for (int x = 0; x < products.size(); x++) {
            System.out.printf("\t" + "%-40s %d %n", products.get(x).getName(), x);
        }
        if (back)
            System.out.printf("\t" + "%-40s %d %n", "Back to main menu", products.size());
        System.out.println("\n\t" + String.format("%40s", "*").replace(' ', '*'));
        System.out.print("\tYour choice : ");

        try {
            option = scan.nextInt();
            scan.nextLine();
        } catch (Exception e) {
            return -1;
        }

        // exit selected
        if (back) {
            if (option == products.size()) {
                return products.size();
            }

            // unknown selection
            if (option > products.size()) {
                return -1;
            }
        } else {
            // unknown selection
            if (option > products.size() - 1) {
                return -1;
            }
        }
        return option;
    }
}
