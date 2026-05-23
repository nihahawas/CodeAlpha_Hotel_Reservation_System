package com.codealpha.hotel.ui;

import com.codealpha.hotel.model.*;
import com.codealpha.hotel.model.Reservation.ReservationStatus;
import com.codealpha.hotel.service.HotelService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * ConsoleUI — Full console interface with ANSI color support.
 * Provides a beautiful, interactive hotel management system.
 */
public class ConsoleUI {

    // ─── ANSI Color Codes ─────────────────────────────────────────────────────
    private static final String RESET   = "\u001B[0m";
    private static final String BOLD    = "\u001B[1m";
    private static final String RED     = "\u001B[31m";
    private static final String GREEN   = "\u001B[32m";
    private static final String YELLOW  = "\u001B[33m";
    private static final String BLUE    = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN    = "\u001B[36m";
    private static final String WHITE   = "\u001B[37m";

    private final HotelService service;
    private final Scanner      scanner;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ConsoleUI() {
        this.service = new HotelService();
        this.scanner = new Scanner(System.in);
    }

    // ─── Main Loop ────────────────────────────────────────────────────────────
    public void start() {
        printWelcomeBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter your choice");
            switch (choice) {
                case 1  -> roomsMenu();
                case 2  -> customersMenu();
                case 3  -> reservationsMenu();
                case 4  -> paymentsMenu();
                case 5  -> reportsMenu();
                case 0  -> { running = false; printBye(); }
                default -> printError("Invalid option! Please try again.");
            }
        }
    }

    // ═══ MAIN MENU ════════════════════════════════════════════════════════════

    private void printMainMenu() {
        System.out.println();
        printDivider("=", CYAN);
        System.out.println(CYAN + BOLD + "             🏨  MAIN MENU" + RESET);
        printDivider("=", CYAN);
        System.out.println(YELLOW + "  [1]" + RESET + "  🛏️   Room Management");
        System.out.println(YELLOW + "  [2]" + RESET + "  👤   Customer Management");
        System.out.println(YELLOW + "  [3]" + RESET + "  📋   Reservation Management");
        System.out.println(YELLOW + "  [4]" + RESET + "  💳   Payment Management");
        System.out.println(YELLOW + "  [5]" + RESET + "  📊   Reports & Analytics");
        System.out.println(RED    + "  [0]" + RESET + "  🚪   Exit");
        printDivider("-", CYAN);
    }

    // ═══ ROOMS MENU ═══════════════════════════════════════════════════════════

    private void roomsMenu() {
        boolean back = false;
        while (!back) {
            printDivider("─", BLUE);
            System.out.println(BLUE + BOLD + "  🛏️  ROOM MANAGEMENT" + RESET);
            printDivider("─", BLUE);
            System.out.println(YELLOW + "  [1]" + RESET + "  View All Rooms");
            System.out.println(YELLOW + "  [2]" + RESET + "  View Available Rooms");
            System.out.println(YELLOW + "  [3]" + RESET + "  Search by Category");
            System.out.println(YELLOW + "  [4]" + RESET + "  View Room Details");
            System.out.println(RED    + "  [0]" + RESET + "  Back to Main Menu");
            printDivider("─", BLUE);

            int c = readInt("Choice");
            switch (c) {
                case 1 -> viewAllRooms();
                case 2 -> viewAvailableRooms();
                case 3 -> searchRoomByCategory();
                case 4 -> viewRoomDetails();
                case 0 -> back = true;
                default -> printError("Invalid option!");
            }
        }
    }

    private void viewAllRooms() {
        try {
            List<Room> rooms = service.getAllRooms();
            printRoomHeader();
            rooms.forEach(this::printRoom);
            printDivider("─", WHITE);
            printSuccess("Total rooms: " + rooms.size());
        } catch (SQLException e) { printError("DB Error: " + e.getMessage()); }
    }

    private void viewAvailableRooms() {
        try {
            List<Room> rooms = service.getAvailableRooms();
            if (rooms.isEmpty()) { printWarning("No rooms currently available."); return; }
            printRoomHeader();
            rooms.forEach(this::printRoom);
            printDivider("─", WHITE);
            printSuccess("Available rooms: " + rooms.size());
        } catch (SQLException e) { printError("DB Error: " + e.getMessage()); }
    }

    private void searchRoomByCategory() {
        System.out.println(CYAN + "\n  Select Category:" + RESET);
        System.out.println("  [1] STANDARD  |  [2] DELUXE  |  [3] SUITE");
        int c = readInt("Choice");
        Room.RoomCategory cat;
        switch (c) {
            case 1 -> cat = Room.RoomCategory.STANDARD;
            case 2 -> cat = Room.RoomCategory.DELUXE;
            case 3 -> cat = Room.RoomCategory.SUITE;
            default -> { printError("Invalid category!"); return; }
        }
        try {
            List<Room> rooms = service.getRoomsByCategory(cat);
            if (rooms.isEmpty()) { printWarning("No rooms in this category."); return; }
            printRoomHeader();
            rooms.forEach(this::printRoom);
        } catch (SQLException e) { printError("DB Error: " + e.getMessage()); }
    }

    private void viewRoomDetails() {
        int id = readInt("Enter Room ID");
        try {
            Room r = service.getRoomById(id);
            if (r == null) { printError("Room not found!"); return; }
            System.out.println();
            printDivider("─", CYAN);
            System.out.println(CYAN + BOLD + "  ROOM DETAILS" + RESET);
            printDivider("─", CYAN);
            System.out.printf("  Room No   : %s%s%s%n", YELLOW, r.getRoomNumber(), RESET);
            System.out.printf("  Category  : %s%s%s%n", MAGENTA, r.getCategory(), RESET);
            System.out.printf("  Price     : %sPKR %.2f / night%s%n", GREEN, r.getPricePerNight(), RESET);
            System.out.printf("  Capacity  : %s%d persons%s%n", WHITE, r.getCapacity(), RESET);
            System.out.printf("  Status    : %s%s%n", r.isAvailable()
                    ? GREEN + "✅ Available" : RED + "❌ Occupied", RESET);
            System.out.printf("  Details   : %s%s%n", WHITE, r.getDescription());
            printDivider("─", CYAN);
        } catch (SQLException e) { printError("DB Error: " + e.getMessage()); }
    }

    // ═══ CUSTOMERS MENU ═══════════════════════════════════════════════════════

    private void customersMenu() {
        boolean back = false;
        while (!back) {
            printDivider("─", MAGENTA);
            System.out.println(MAGENTA + BOLD + "  👤  CUSTOMER MANAGEMENT" + RESET);
            printDivider("─", MAGENTA);
            System.out.println(YELLOW + "  [1]" + RESET + "  Register New Customer");
            System.out.println(YELLOW + "  [2]" + RESET + "  View All Customers");
            System.out.println(YELLOW + "  [3]" + RESET + "  Search Customer by Email");
            System.out.println(YELLOW + "  [4]" + RESET + "  View Customer by ID");
            System.out.println(RED    + "  [0]" + RESET + "  Back");
            printDivider("─", MAGENTA);

            int c = readInt("Choice");
            switch (c) {
                case 1 -> registerCustomer();
                case 2 -> viewAllCustomers();
                case 3 -> searchCustomerByEmail();
                case 4 -> viewCustomerById();
                case 0 -> back = true;
                default -> printError("Invalid option!");
            }
        }
    }

    private void registerCustomer() {
        System.out.println(CYAN + "\n  ── Register New Customer ──" + RESET);
        String name    = readString("Full Name");
        String email   = readString("Email");
        String phone   = readString("Phone");
        String address = readString("Address");
        try {
            if (service.customerExists(email)) {
                printError("A customer with this email already exists!");
                return;
            }
            Customer c = new Customer(name, email, phone, address);
            service.registerCustomer(c);
            printSuccess("✅ Customer registered successfully! ID: " + c.getId());
        } catch (SQLException e) { printError("DB Error: " + e.getMessage()); }
    }

    private void viewAllCustomers() {
        try {
            List<Customer> list = service.getAllCustomers();
            if (list.isEmpty()) { printWarning("No customers found."); return; }
            System.out.println();
            System.out.printf(BOLD + "  %-5s %-25s %-30s %-15s%n" + RESET,
                    "ID", "Name", "Email", "Phone");
            printDivider("─", WHITE);
            for (Customer c : list) {
                System.out.printf("  %-5d %-25s %-30s %-15s%n",
                        c.getId(), c.getName(), c.getEmail(), c.getPhone());
            }
            printDivider("─", WHITE);
        } catch (SQLException e) { printError("DB Error: " + e.getMessage()); }
    }

    private void searchCustomerByEmail() {
        String email = readString("Email to search");
        try {
            Customer c = service.getCustomerByEmail(email);
            if (c == null) { printError("Customer not found!"); return; }
            printCustomerDetails(c);
        } catch (SQLException e) { printError("DB Error: " + e.getMessage()); }
    }

    private void viewCustomerById() {
        int id = readInt("Customer ID");
        try {
            Customer c = service.getCustomerById(id);
            if (c == null) { printError("Customer not found!"); return; }
            printCustomerDetails(c);
        } catch (SQLException e) { printError("DB Error: " + e.getMessage()); }
    }

    private void printCustomerDetails(Customer c) {
        System.out.println();
        printDivider("─", MAGENTA);
        System.out.printf("  ID      : %s%d%s%n",      YELLOW, c.getId(), RESET);
        System.out.printf("  Name    : %s%s%s%n",      CYAN, c.getName(), RESET);
        System.out.printf("  Email   : %s%s%s%n",      WHITE, c.getEmail(), RESET);
        System.out.printf("  Phone   : %s%s%s%n",      WHITE, c.getPhone(), RESET);
        System.out.printf("  Address : %s%s%s%n",      WHITE, c.getAddress(), RESET);
        printDivider("─", MAGENTA);
    }

    // ═══ RESERVATIONS MENU ════════════════════════════════════════════════════

    private void reservationsMenu() {
        boolean back = false;
        while (!back) {
            printDivider("─", GREEN);
            System.out.println(GREEN + BOLD + "  📋  RESERVATION MANAGEMENT" + RESET);
            printDivider("─", GREEN);
            System.out.println(YELLOW + "  [1]" + RESET + "  Book a Room");
            System.out.println(YELLOW + "  [2]" + RESET + "  Cancel Reservation");
            System.out.println(YELLOW + "  [3]" + RESET + "  Check-In Guest");
            System.out.println(YELLOW + "  [4]" + RESET + "  Check-Out Guest");
            System.out.println(YELLOW + "  [5]" + RESET + "  View All Reservations");
            System.out.println(YELLOW + "  [6]" + RESET + "  View Reservation Details");
            System.out.println(YELLOW + "  [7]" + RESET + "  View by Customer ID");
            System.out.println(RED    + "  [0]" + RESET + "  Back");
            printDivider("─", GREEN);

            int c = readInt("Choice");
            switch (c) {
                case 1 -> bookRoom();
                case 2 -> cancelReservation();
                case 3 -> checkIn();
                case 4 -> checkOut();
                case 5 -> viewAllReservations();
                case 6 -> viewReservationDetails();
                case 7 -> viewReservationsByCustomer();
                case 0 -> back = true;
                default -> printError("Invalid option!");
            }
        }
    }

    private void bookRoom() {
        System.out.println(CYAN + "\n  ── Book a Room ──" + RESET);

        // Show available rooms first
        viewAvailableRooms();

        int roomId = readInt("Enter Room ID to book");
        int customerId = readInt("Enter Customer ID");
        LocalDate checkIn  = readDate("Check-in Date  (yyyy-MM-dd)");
        LocalDate checkOut = readDate("Check-out Date (yyyy-MM-dd)");

        try {
            Reservation r = service.bookRoom(customerId, roomId, checkIn, checkOut);
            System.out.println();
            printDivider("═", GREEN);
            printSuccess("  ✅ BOOKING CONFIRMED!");
            printDivider("═", GREEN);
            System.out.printf("  Reservation ID : %s%d%s%n",       YELLOW, r.getId(), RESET);
            System.out.printf("  Customer       : %s%s%s%n",       CYAN, r.getCustomerName(), RESET);
            System.out.printf("  Room           : %s%s (%s)%s%n",  MAGENTA, r.getRoomNumber(), r.getRoomCategory(), RESET);
            System.out.printf("  Check-In       : %s%s%s%n",       WHITE, r.getCheckInDate(), RESET);
            System.out.printf("  Check-Out      : %s%s%s%n",       WHITE, r.getCheckOutDate(), RESET);
            System.out.printf("  Total Nights   : %s%d%s%n",       WHITE, r.getTotalNights(), RESET);
            System.out.printf("  Total Amount   : %sPKR %.2f%s%n", GREEN, r.getTotalAmount(), RESET);
            System.out.printf("  Status         : %sCONFIRMED%s%n", GREEN, RESET);
            printDivider("═", GREEN);
            System.out.println(CYAN + "  💡 Proceed to Payment menu to complete payment." + RESET);
        } catch (Exception e) { printError(e.getMessage()); }
    }

    private void cancelReservation() {
        int id = readInt("Enter Reservation ID to cancel");
        System.out.print(YELLOW + "  ⚠️  Are you sure? (yes/no): " + RESET);
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes")) { printWarning("Cancellation aborted."); return; }
        try {
            service.cancelReservation(id);
            printSuccess("✅ Reservation #" + id + " cancelled. Room is now available. Refund issued if applicable.");
        } catch (Exception e) { printError(e.getMessage()); }
    }

    private void checkIn() {
        int id = readInt("Reservation ID for Check-In");
        try {
            service.checkIn(id);
            printSuccess("✅ Guest checked in successfully for Reservation #" + id);
        } catch (Exception e) { printError(e.getMessage()); }
    }

    private void checkOut() {
        int id = readInt("Reservation ID for Check-Out");
        try {
            service.checkOut(id);
            printSuccess("✅ Guest checked out successfully. Room is now available. Thank you!");
        } catch (Exception e) { printError(e.getMessage()); }
    }

    private void viewAllReservations() {
        try {
            List<Reservation> list = service.getAllReservations();
            if (list.isEmpty()) { printWarning("No reservations found."); return; }
            printReservationHeader();
            list.forEach(this::printReservation);
            printDivider("─", WHITE);
            printSuccess("Total: " + list.size() + " reservations");
        } catch (SQLException e) { printError("DB Error: " + e.getMessage()); }
    }

    private void viewReservationDetails() {
        int id = readInt("Reservation ID");
        try {
            Reservation r = service.getReservationById(id);
            if (r == null) { printError("Reservation not found!"); return; }
            System.out.println();
            printDivider("─", CYAN);
            System.out.println(CYAN + BOLD + "  RESERVATION DETAILS" + RESET);
            printDivider("─", CYAN);
            System.out.printf("  Reservation ID : %s%d%s%n",       YELLOW, r.getId(), RESET);
            System.out.printf("  Customer       : %s%s (ID: %d)%s%n", CYAN, r.getCustomerName(), r.getCustomerId(), RESET);
            System.out.printf("  Room           : %s%s (%s)%s%n",  MAGENTA, r.getRoomNumber(), r.getRoomCategory(), RESET);
            System.out.printf("  Check-In       : %s%s%s%n",       WHITE, r.getCheckInDate(), RESET);
            System.out.printf("  Check-Out      : %s%s%s%n",       WHITE, r.getCheckOutDate(), RESET);
            System.out.printf("  Total Nights   : %s%d%s%n",       WHITE, r.getTotalNights(), RESET);
            System.out.printf("  Total Amount   : %sPKR %.2f%s%n", GREEN, r.getTotalAmount(), RESET);
            String statusColor = switch (r.getStatus()) {
                case CONFIRMED -> GREEN; case CANCELLED -> RED;
                case CHECKED_IN -> CYAN; case CHECKED_OUT -> MAGENTA; default -> YELLOW;
            };
            System.out.printf("  Status         : %s%s%s%n", statusColor, r.getStatus(), RESET);
            printDivider("─", CYAN);
        } catch (SQLException e) { printError("DB Error: " + e.getMessage()); }
    }

    private void viewReservationsByCustomer() {
        int cid = readInt("Customer ID");
        try {
            List<Reservation> list = service.getReservationsByCustomer(cid);
            if (list.isEmpty()) { printWarning("No reservations found for this customer."); return; }
            printReservationHeader();
            list.forEach(this::printReservation);
        } catch (SQLException e) { printError("DB Error: " + e.getMessage()); }
    }

    // ═══ PAYMENTS MENU ════════════════════════════════════════════════════════

    private void paymentsMenu() {
        boolean back = false;
        while (!back) {
            printDivider("─", YELLOW);
            System.out.println(YELLOW + BOLD + "  💳  PAYMENT MANAGEMENT" + RESET);
            printDivider("─", YELLOW);
            System.out.println(YELLOW + "  [1]" + RESET + "  Process Payment");
            System.out.println(YELLOW + "  [2]" + RESET + "  View All Payments");
            System.out.println(RED    + "  [0]" + RESET + "  Back");
            printDivider("─", YELLOW);

            int c = readInt("Choice");
            switch (c) {
                case 1 -> processPayment();
                case 2 -> viewAllPayments();
                case 0 -> back = true;
                default -> printError("Invalid option!");
            }
        }
    }

    private void processPayment() {
        System.out.println(CYAN + "\n  ── Process Payment ──" + RESET);
        int resId = readInt("Enter Reservation ID");

        System.out.println(CYAN + "\n  Select Payment Method:" + RESET);
        System.out.println("  [1] 💵 Cash");
        System.out.println("  [2] 💳 Credit Card");
        System.out.println("  [3] 💳 Debit Card");
        System.out.println("  [4] 🌐 Online Transfer");

        int c = readInt("Choice");
        Payment.PaymentMethod method = switch (c) {
            case 1 -> Payment.PaymentMethod.CASH;
            case 2 -> Payment.PaymentMethod.CREDIT_CARD;
            case 3 -> Payment.PaymentMethod.DEBIT_CARD;
            case 4 -> Payment.PaymentMethod.ONLINE;
            default -> { printError("Invalid method!"); yield null; }
        };
        if (method == null) return;

        try {
            // Show simulated processing
            System.out.println(YELLOW + "\n  ⏳ Processing payment..." + RESET);
            Thread.sleep(1500);

            Payment p = service.processPayment(resId, method);
            System.out.println();
            printDivider("═", GREEN);
            printSuccess("  ✅ PAYMENT SUCCESSFUL!");
            printDivider("═", GREEN);
            System.out.printf("  Transaction ID : %s%s%s%n",       YELLOW, p.getTransactionId(), RESET);
            System.out.printf("  Amount Paid    : %sPKR %.2f%s%n", GREEN, p.getAmount(), RESET);
            System.out.printf("  Method         : %s%s%s%n",       CYAN, p.getPaymentMethod(), RESET);
            System.out.printf("  Status         : %sCOMPLETED%s%n",GREEN, RESET);
            System.out.printf("  Date & Time    : %s%s%s%n",       WHITE, p.getPaymentDate(), RESET);
            printDivider("═", GREEN);
        } catch (InterruptedException ignored) {
        } catch (Exception e) { printError(e.getMessage()); }
    }

    private void viewAllPayments() {
        try {
            List<Payment> list = service.getAllPayments();
            if (list.isEmpty()) { printWarning("No payments found."); return; }
            System.out.println();
            System.out.printf(BOLD + "  %-5s %-6s %-20s %-15s %-12s %-12s%n" + RESET,
                    "ID", "Res#", "Transaction", "Amount", "Method", "Status");
            printDivider("─", WHITE);
            for (Payment p : list) {
                String sc = p.getPaymentStatus() == Payment.PaymentStatus.COMPLETED ? GREEN
                          : p.getPaymentStatus() == Payment.PaymentStatus.REFUNDED  ? YELLOW : RED;
                System.out.printf("  %-5d %-6d %-20s %-15s %-12s %s%-12s%s%n",
                        p.getId(), p.getReservationId(), p.getTransactionId(),
                        String.format("PKR %.0f", p.getAmount()),
                        p.getPaymentMethod(), sc, p.getPaymentStatus(), RESET);
            }
            printDivider("─", WHITE);
        } catch (SQLException e) { printError("DB Error: " + e.getMessage()); }
    }

    // ═══ REPORTS MENU ═════════════════════════════════════════════════════════

    private void reportsMenu() {
        try {
            List<Reservation> all          = service.getAllReservations();
            List<Reservation> confirmed    = service.getReservationsByStatus(ReservationStatus.CONFIRMED);
            List<Reservation> checkedIn    = service.getReservationsByStatus(ReservationStatus.CHECKED_IN);
            List<Reservation> checkedOut   = service.getReservationsByStatus(ReservationStatus.CHECKED_OUT);
            List<Reservation> cancelled    = service.getReservationsByStatus(ReservationStatus.CANCELLED);
            double revenue                 = service.getTotalRevenue();

            System.out.println();
            printDivider("═", CYAN);
            System.out.println(CYAN + BOLD + "       📊  HOTEL ANALYTICS REPORT" + RESET);
            printDivider("═", CYAN);
            System.out.printf("  Total Reservations   : %s%d%s%n",         YELLOW, all.size(), RESET);
            System.out.printf("  Confirmed            : %s%d%s%n",         GREEN, confirmed.size(), RESET);
            System.out.printf("  Currently Checked-In : %s%d%s%n",         CYAN, checkedIn.size(), RESET);
            System.out.printf("  Checked-Out          : %s%d%s%n",         MAGENTA, checkedOut.size(), RESET);
            System.out.printf("  Cancelled            : %s%d%s%n",         RED, cancelled.size(), RESET);
            printDivider("─", CYAN);
            System.out.printf("  💰 Total Revenue     : %sPKR %.2f%s%n",   GREEN + BOLD, revenue, RESET);
            printDivider("═", CYAN);

            // Available rooms count
            List<Room> available = service.getAvailableRooms();
            List<Room> allRooms  = service.getAllRooms();
            System.out.printf("  Total Rooms          : %s%d%s%n",  WHITE, allRooms.size(), RESET);
            System.out.printf("  Available Now        : %s%d%s%n",  GREEN, available.size(), RESET);
            System.out.printf("  Occupied Now         : %s%d%s%n",  RED, allRooms.size() - available.size(), RESET);
            printDivider("═", CYAN);
        } catch (SQLException e) { printError("DB Error: " + e.getMessage()); }
    }

    // ═══ PRINT HELPERS ════════════════════════════════════════════════════════

    private void printRoomHeader() {
        System.out.println();
        System.out.printf(BOLD + "  %-5s %-8s %-12s %-14s %-10s %-12s%n" + RESET,
                "ID", "Room", "Category", "Price/Night", "Capacity", "Status");
        printDivider("─", WHITE);
    }

    private void printRoom(Room r) {
        String statusStr = r.isAvailable()
                ? GREEN + "✅ Available" + RESET
                : RED   + "❌ Occupied"  + RESET;
        String catColor = switch (r.getCategory()) {
            case STANDARD -> WHITE;
            case DELUXE   -> CYAN;
            case SUITE    -> YELLOW;
        };
        System.out.printf("  %-5d %-8s %s%-12s%s %-14s %-10d %s%n",
                r.getId(), r.getRoomNumber(),
                catColor, r.getCategory(), RESET,
                String.format("PKR %.0f", r.getPricePerNight()),
                r.getCapacity(), statusStr);
    }

    private void printReservationHeader() {
        System.out.println();
        System.out.printf(BOLD + "  %-5s %-20s %-8s %-12s %-12s %-8s %-14s %-12s%n" + RESET,
                "ID", "Customer", "Room", "Check-In", "Check-Out", "Nights", "Amount", "Status");
        printDivider("─", WHITE);
    }

    private void printReservation(Reservation r) {
        String sc = switch (r.getStatus()) {
            case CONFIRMED   -> GREEN;
            case CANCELLED   -> RED;
            case CHECKED_IN  -> CYAN;
            case CHECKED_OUT -> MAGENTA;
            default          -> YELLOW;
        };
        String name = r.getCustomerName() != null
                ? (r.getCustomerName().length() > 18
                        ? r.getCustomerName().substring(0, 15) + "..." : r.getCustomerName())
                : "N/A";
        System.out.printf("  %-5d %-20s %-8s %-12s %-12s %-8d %-14s %s%-12s%s%n",
                r.getId(), name, r.getRoomNumber(),
                r.getCheckInDate(), r.getCheckOutDate(),
                r.getTotalNights(),
                String.format("PKR %.0f", r.getTotalAmount()),
                sc, r.getStatus(), RESET);
    }

    private void printWelcomeBanner() {
        System.out.println(CYAN + BOLD);
        System.out.println("  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║                                                  ║");
        System.out.println("  ║     🏨  GRAND HORIZON HOTEL                     ║");
        System.out.println("  ║         Reservation Management System            ║");
        System.out.println("  ║         CodeAlpha Java Internship — Task 4       ║");
        System.out.println("  ║                                                  ║");
        System.out.println("  ╚══════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    private void printBye() {
        System.out.println(CYAN + BOLD);
        System.out.println("  Thank you for using Grand Horizon Hotel System!");
        System.out.println("  Goodbye! 👋" + RESET);
    }

    private void printDivider(String ch, String color) {
        System.out.println(color + "  " + ch.repeat(56) + RESET);
    }

    private void printSuccess(String msg) { System.out.println(GREEN + "  " + msg + RESET); }
    private void printError(String msg)   { System.out.println(RED   + "  ❌ " + msg + RESET); }
    private void printWarning(String msg) { System.out.println(YELLOW + "  ⚠️  " + msg + RESET); }

    // ─── Input helpers ────────────────────────────────────────────────────────

    private int readInt(String prompt) {
        while (true) {
            System.out.print(CYAN + "  ➤ " + prompt + ": " + RESET);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                printError("Please enter a valid number!");
            }
        }
    }

    private String readString(String prompt) {
        System.out.print(CYAN + "  ➤ " + prompt + ": " + RESET);
        return scanner.nextLine().trim();
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(CYAN + "  ➤ " + prompt + ": " + RESET);
            try {
                return LocalDate.parse(scanner.nextLine().trim(), DATE_FMT);
            } catch (DateTimeParseException e) {
                printError("Invalid date! Use format: yyyy-MM-dd (e.g. 2026-06-15)");
            }
        }
    }
}
