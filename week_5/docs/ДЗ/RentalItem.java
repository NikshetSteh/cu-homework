import java.util.Objects;

public abstract class RentalItem {
    private final String inventoryCode;
    private final String name;
    private final int dailyRate;

    public RentalItem(String inventoryCode, String name, int dailyRate) {
        if (dailyRate <= 0) {
            throw new IllegalArgumentException(String.format("Invalid daily rate: %d", dailyRate));
        }

        this.inventoryCode = Objects.requireNonNull(inventoryCode);
        this.name = Objects.requireNonNull(name);
        this.dailyRate = dailyRate;
    }

    public abstract int calcRentCost(int period);

    @Override
    public String toString() {
        return String.format(
                "%s (%s): %d per day",
                this.name,
                this.inventoryCode,
                this.dailyRate
        );
    }

    public String getName() {
        return name;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public int getDailyRate() {
        return dailyRate;
    }
}