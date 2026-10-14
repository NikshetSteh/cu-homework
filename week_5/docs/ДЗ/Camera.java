public class Camera extends RentalItem implements IDepositable {
    private final int megapixels;

    public Camera(String inventoryCode, String name, int dailyRate, int megapixels) {
        if (megapixels <= 0) {
            throw new IllegalArgumentException(String.format("Invalid megapixelsL %d", megapixels));
        }

        super(inventoryCode, name, dailyRate);

        this.megapixels = megapixels;
    }

    @Override
    public int getDeposit() {
        return 15_000;
    }

    @Override
    public int calcRentCost(int period) {
        if (period <= 0) {
            throw new IllegalArgumentException(String.format("Invalid rent period: %d", period));
        }

        return 500 + this.getDailyRate() * period;
    }

    public int getMegapixels() {
        return megapixels;
    }
}
