package au.edu.rmit.chaos.menu;

import java.util.ArrayList;
import java.util.Scanner;

import com.mashape.unirest.http.exceptions.UnirestException;
import au.edu.rmit.chaos.*;

public class CustomerMenu extends Menu {
    private Customer customer;
    private Scanner scan;
    private ArrayList<Product> products;
    private Order order;

    public CustomerMenu(Customer cu) {
        this.customer = cu;
        scan = new Scanner(System.in);
    }

    private void printInvoic(OrderStatus status) {
        if (status == OrderStatus.placed || status == OrderStatus.already_placed)
            System.out.println("\n\n\tOrder Placed Successfully 😃✌️!\n");
        else if (status == OrderStatus.canceled || status == OrderStatus.already_canceled) {
            System.out.println("\n\n\tOrder has been canceled️!\n");
            System.out.println("\n\n\tPress return to go back to main menu...");
            scan.nextLine();
            return;
        }

        System.out.printf("\t" + "%-40s %-10s %-10s %-10s %n", "Order (#" + order.getID() + ")", "qty", "unit price",
                "total");
        for (OrderItem item : order.getOrderItems()) {
            System.out.printf("\t" + "%-40s %-10s $%-10s $%-10s %n", item.getProduct().getName(), item.getQuantity(),
                    item.getProduct().getUnitPrice(), item.getTotal());
        }
        System.out.println("\t--------------------------------------------------------------");
        System.out.printf("\t" + "%-40s %-10s %-10s $%-10s %n", "Subtotal", " ", " ",
                order.getSubtotal() + order.getDiscount());
        System.out.printf("\t" + "%-40s %-10s %-10s $%-10s %n", "Discount", " ", " ",
                order.getPointsDiscount() + order.getDiscount());
        System.out.printf("\t" + "%-40s %-10s %-10s $%-10s %n", "Total", " ", " ", order.getTotal());

        System.out.println("\n\n\tPress return to go back to main menu...");
        scan.nextLine();
    }

    private void loadProducts() {
        products = Product.fetchProductsFromServer();
    }

    private void checkApplicableDiscunts() {
        products = Product.fetchProductsFromServer();
        displayDiscount(products);
        pressToContinue();
    }

    private void checkProductPriceMenu() {
        products = Product.fetchProductsFromServer();
        do {
            int option = displayProductsInput(products, "Available Products", true);
            if (option == products.size())
                break;
            else if (option == -1) {
                invalidOptionMessage();
                pressToContinue();
                continue;
            }

            Product pr = products.get(option);
            System.out.printf("%n\t1 %s of %s costs $%.2f", pr.getType().toString(), pr.getName(), pr.getUnitPrice());
            pressToContinue();
        } while (true);
    }

    private void placeOrderMenu() {
        int moreProducts;

        // new order
        if (order == null) {
            System.out.println("\n\tPlease Wait...\n");
            loadProducts();
            try {
                order = new Order(customer);
                order.createNewOrder();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }

        do {
            String title = "Available Products (Order #" + order.getID() + ")";
            int item = displayProductsInput(products, title, false);

            if (item == -1) {
                invalidOptionMessage();
                pressToContinue();
                continue;
            }

            Product pr = products.get(item);
            title = "Enter quantity for " + pr.getName();
            int qty = displayIntegerInputMessage(title);

            if (qty == 0) {
                continue;
            }

            this.order.addProduct(products.get(item), qty);

            do {
                System.out.print("\tDo you want to add more products? Y/N: ");
                moreProducts = scan.nextLine().toLowerCase().trim().charAt(0);
            } while (moreProducts != 'y' && moreProducts != 'n');

            // more products
            if (moreProducts == 'n') {
                OrderStatus status = this.order.placeOrder();
                while (status == OrderStatus.pending || status == OrderStatus.insufficient_balance
                        || status == OrderStatus.unknown) {
                    System.out.println("\n\tInsufficient balance");
                    System.out.println("\tReference order #" + order.getID() + " to the sales staff.");
                    System.out.println("\tPlease wait for sales staff assistant 🙁!");
                    scan.nextLine();
                    status = this.order.placeOrder();
                }
                printInvoic(status);
                break;
            }
        } while (true);

    }

    public void display() {
        int option = 0;

        do {
            System.out.println("\n\n\t\t\tWelcome " + customer.getName());
            System.out.printf("\t%-40s %-10s %n", "Place Order", "0");
            System.out.printf("\t%-40s %-10s %n", "Check Product Price", "1");
            System.out.printf("\t%-40s %-10s %n", "Check Applicable Discount", "2");
            System.out.printf("\t%-40s %-10s %n", "Exit", "3");
            System.out.println(String.format("%n\t%-53s%n", " ").replace(" ", "*"));
            System.out.print("\tYour choice : ");

            try {
                option = scan.nextInt();
                scan.nextLine();
            } catch (Exception ignored) {
            }
            if (option == 0) {
                placeOrderMenu();
                this.order = null;
            } else if (option == 1) {
                checkProductPriceMenu();
            } else if (option == 2) {
                checkApplicableDiscunts();
            } else if (option == 3) {
                System.exit(0);
            }

        } while (true);
    }
}
