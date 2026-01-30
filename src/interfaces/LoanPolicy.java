package interfaces;

public interface LoanPolicy {
    int getMaxLoanDays();

    double getDailyFine();

    boolean isRenewable();

    int getMaxRenewals();
}
