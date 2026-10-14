public class Laptop extends RentalItem implements IDepositable {
    private final int ramGb;

    public Laptop(String inventoryCode, String name, int dailyRate, int ramGb) {
        if (ramGb <= 0) {
            throw new IllegalArgumentException(String.format("Invalid ram: %d", ramGb));
        }

        super(inventoryCode, name, dailyRate);
        this.ramGb = ramGb;
    }

    @Override
    public int calcRentCost(int period) {
        if (period <= 0) {
            throw new IllegalArgumentException(String.format("Invalid rent period: %d", period));
        }

        return this.getDailyRate() * period;
    }


    @Override
    public int getDeposit() {
        return 20_000;
    }

    public int getRamGb() {
        return ramGb;
    }
}
