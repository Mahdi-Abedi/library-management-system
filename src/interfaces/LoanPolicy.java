package interfaces;

public interface LoanPolicy {
    int getMaxLoanDays();

    double getDailyFine();

    boolean isRenewable();

    int getMaxRenewals();

    default String getLoanInfo() {
        return String.format("Max loan: %d days, Daily fine: %.2f, Renewable: %s",
                getMaxLoanDays(), getDailyFine(), isRenewable());
    }
}
