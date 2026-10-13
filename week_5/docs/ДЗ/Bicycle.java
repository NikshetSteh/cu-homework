public class Bicycle extends RentalItem {
    private final boolean electric;

    public Bicycle(String inventoryCode, String name, int dailyRate, boolean electric) {
        super(inventoryCode, name, dailyRate);
        this.electric = electric;
    }

    @Override
    public int calcRentCost(int period) {
        if (period <= 0) {
            throw new IllegalArgumentException(String.format("Invalid rent period: %d", period));
        }

        return period * this.getDailyRate() + (this.electric ? 300 * period : 0);
    }

    public boolean isElectric() {
        return electric;
    }
}
