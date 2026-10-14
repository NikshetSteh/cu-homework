public record RentalOrder(Customer customer, RentalItem[] items, int period) {
    public RentalOrder(Customer customer, RentalItem[] items, int period) {
        if (customer == null) {
            throw new IllegalArgumentException("Null customer");
        }

        if (items == null) {
            throw new IllegalArgumentException("Null items");
        }

        for (RentalItem item : items) {
            if (item == null) {
                throw new IllegalArgumentException("Null item");
            }
        }

        if (period <= 0) {
            throw new IllegalArgumentException(String.format("Invalid period: %d", period));
        }

        this.customer = customer;
        this.items = items.clone();
        this.period = period;
    }

    public int calcRentCost() {
        int sum = 0;
        for (RentalItem rentalItem : this.items) {
            sum += rentalItem.calcRentCost(this.period);
        }

        return sum;
    }

    public int calcDeposit() {
        int sum = 0;
        for (RentalItem rentalItem : this.items) {
            if (rentalItem instanceof IDepositable) {
                sum += ((IDepositable) rentalItem).getDeposit();
            }
        }

        return sum;
    }

    public RentalItem getMostExpensiveItem() {
        RentalItem mostExpensiveItem = null;
        int maxCost = Integer.MIN_VALUE;
        for (RentalItem item : this.items) {
            int itemCost = item.calcRentCost(this.period);
            if (itemCost > maxCost) {
                maxCost = itemCost;
                mostExpensiveItem = item;
            }
        }

        return mostExpensiveItem;
    }

    @Override
    public String toString() {
        String itemsDescription = "";
        for (RentalItem item : items) {
            itemsDescription = itemsDescription.concat(item.toString()) + "\n";
        }
        itemsDescription = itemsDescription.strip();

        return String.format(
                """
                        Customer: %s
                        Rental: %d
                        Items:
                        %s
                        """.strip(),
                this.customer.toString(),
                this.period,
                itemsDescription
        );
    }

    @Override
    public RentalItem[] items() {
        return this.items.clone();
    }

    public int countItemsByThreshold(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException(String.format("Invalid threshold: %d", threshold));
        }

        int counter = 0;
        for (RentalItem item : this.items) {
            if (item.calcRentCost(this.period) >= threshold) {
                counter += 1;
            }
        }

        return counter;
    }
}
