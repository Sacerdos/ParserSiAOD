public class Record {
    private int Nominal;
    private String Date;
    private double Value;
    public int getNominal() {
        return Nominal;
    }
    public void setNominal(int Nominal) {
        this.Nominal = Nominal;
    }
    public double getValue() {
        return Value;
    }
    public void setValue(double Value) {
        this.Value = Value;
    }
    public String getDate() {
        return Date;
    }
    public void setDate(String Date) {
        this.Date = Date;
    }

    @Override
    public String toString() {
        return "Record: Date = " + this.Date + " Nominal = " + this.Nominal + " Value = " + this.Value;
    }
}