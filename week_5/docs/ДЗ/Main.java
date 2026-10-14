void demonstratePolymorphism(RentalItem[] items, int days, int threshold) {
    IO.println("Days: " + days + ", threshold: " + threshold);

    int total = 0;
    int atLeastThreshold = 0;
    RentalItem mostExpensive = null;
    int maxCost = Integer.MIN_VALUE;

    for (RentalItem item : items) {
        int cost = item.calcRentCost(days);

        IO.println(item.getName() + ": " + cost);

        total += cost;

        if (cost > maxCost) {
            maxCost = cost;
            mostExpensive = item;
        }

        if (cost >= threshold) {
            atLeastThreshold++;
        }
    }

    IO.println("Total: " + total);
    IO.println("Most expensive: "
            + (mostExpensive == null ? "none" : mostExpensive.getName()));
    IO.println("At least " + threshold + ": " + atLeastThreshold);
}

int totalDeposit(RentalItem[] items) {
    int sum = 0;

    for (RentalItem item : items) {
        if (item instanceof IDepositable) {
            sum += ((IDepositable) item).getDeposit();
        }
    }

    return sum;
}

void demonstrateDepositInterface(RentalItem[] items) {
    for (RentalItem item : items) {
        if (item instanceof IDepositable) {
            IDepositable depositable = (IDepositable) item;
            IO.println(item.getName() + " deposit: " + depositable.getDeposit());
        } else {
            IO.println(item.getName() + " no deposit");
        }
    }
}

void printOrder(RentalOrder order) {
    IO.println(order.toString());
    IO.println("Rental cost: " + order.calcRentCost());
    IO.println("Deposit: " + order.calcDeposit());

    RentalItem mostExpensive = order.getMostExpensiveItem();
    IO.println("Most expensive: " + (mostExpensive == null ? "none" : mostExpensive.getName()));

    IO.println();
}

void checkCost(RentalItem item, int days, int expected) {
    int actual = item.calcRentCost(days);

    IO.println(
            item.getName()
                    + ": actual=" + actual
                    + ", expected=" + expected
                    + ", " + (actual == expected ? "OK" : "FAIL")
    );
}


void main() {
    IO.println("Каталог и расчёт");

    Laptop laptop1 = new Laptop("L-101", "ThinkPad", 1500, 16);
    Laptop laptop2 = new Laptop("L-102", "MacBook Air", 2000, 8);

    Camera camera1 = new Camera("C-205", "Sony", 2000, 24);
    Camera camera2 = new Camera("C-206", "Canon", 1800, 32);

    Bicycle bicycle1 = new Bicycle("B-330", "City Bike", 700, false);
    Bicycle bicycle2 = new Bicycle("B-331", "E-Bike", 900, true);

    RentalItem[] catalog = {
            laptop1,
            camera1,
            bicycle1,
            laptop2,
            camera2,
            bicycle2
    };

    int days = 3;
    int threshold = 4000;

    demonstratePolymorphism(catalog, days, threshold);

    IO.println("\nЗалоги");

    RentalItem[] depositItems = {
            laptop1,
            camera1,
            bicycle1
    };

    IO.println("Total deposit: " + totalDeposit(depositItems));
    demonstrateDepositInterface(depositItems);

    IO.println("\nЗаказы аренды");

    Customer anna = new Customer(42, "Anna", "anna@example.com");
    Customer ivan = new Customer(77, "Ivan", "ivan@example.com");

    RentalItem[] order1Items = {
            laptop1,
            camera1,
            bicycle1
    };

    RentalItem[] order2Items = {
            laptop2,
            camera2,
            bicycle2
    };

    RentalOrder order1 = new RentalOrder(anna, order1Items, 3);
    RentalOrder order2 = new RentalOrder(ivan, order2Items, 5);

    printOrder(order1);
    printOrder(order2);

    IO.println("\nРавенство Customer");

    Customer first = new Customer(42, "Anna", "anna@example.com");
    Customer second = new Customer(42, "Anna Petrova", "new-email@example.com");
    Customer third = new Customer(77, "Ivan", "ivan@example.com");

    IO.println("first == second: " + (first == second));
    IO.println("first.equals(second): " + first.equals(second));
    IO.println("first.hashCode() == second.hashCode(): " + (first.hashCode() == second.hashCode()));
    IO.println("first.equals(third): " + first.equals(third));
    IO.println("first.getClass(): " + first.getClass());
    IO.println("first.toString(): " + first);

    checkCost(new Laptop("L-1", "Laptop", 1000, 8), 1, 1000);
    checkCost(new Camera("C-1", "Camera", 1000, 12), 1, 1500);
    checkCost(new Bicycle("B-1", "Bicycle", 500, false), 2, 1000);
    checkCost(new Bicycle("B-2", "E-Bicycle", 500, true), 2, 1600);
}
