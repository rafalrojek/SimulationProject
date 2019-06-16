package model;

public class Car {

    public static final String ON = "ON";
    public static final String GAS = "GAS";
    public static final String PETROL = "PETROL";

    public static final String DISTRIBUTOR_CODE = "D";
    public static final String CASH_CODE = "M";
    public static final String WASH_CODE = "W";
    public static final String CAR_CODE = "C";
    public static final String TANKS_CODE = "T";


    private int idCar = -1;
    private String tanks;
    private boolean washing;
    private int distributorId = -1;
    private int cashBox = -1;

    public int getIdCar() {
        return idCar;
    }

    public void setIdCar(int idCar) {
        this.idCar = idCar;
    }

    public String getTanks() {
        return tanks;
    }

    public void setTanks(String tanks) {
        this.tanks = tanks;
    }

    public boolean isWashing() {
        return washing;
    }

    public void setWashing(boolean washing) {
        this.washing = washing;
    }

    public int getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(int distributorId) {
        this.distributorId = distributorId;
    }

    public int getCashBox() {
        return cashBox;
    }

    public void setCashBox(int cashBox) {
        this.cashBox = cashBox;
    }

    @Override
    public String toString() {
        return "Car{" +
                "idCar=" + idCar +
                ", tanks='" + tanks + '\'' +
                ", washing=" + washing +
                ", distributorId=" + distributorId +
                ", cashBox=" + cashBox +
                '}';
    }
}
