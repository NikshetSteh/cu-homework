import java.util.Objects;

public record Customer(int id, String name, String email) {
    public Customer(int id, String name, String email) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof Customer)) {
            return false;
        }

        return Objects.equals(((Customer) other).id(), this.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("Customer[id=%d, name=%s, email=%s]", this.id, this.name, this.email);
    }
}
