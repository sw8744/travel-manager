
package kr.ac.konkuk;

public class City {
    private int id;
    private String name;
    private int countryId;
    private String electricityNote;
    private String safetyNote;
    private String foodNote;
    private String climateNote;
    private String transportNote;
    private String travelAlertNote;

    public City(int id, String name, int countryId,
                String electricityNote, String safetyNote,
                String foodNote, String climateNote,
                String transportNote, String travelAlertNote) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;
        this.electricityNote = electricityNote;
        this.safetyNote = safetyNote;
        this.foodNote = foodNote;
        this.climateNote = climateNote;
        this.transportNote = transportNote;
        this.travelAlertNote = travelAlertNote;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCountryId() {
        return countryId;
    }

    public String getElectricityNote() {
        return electricityNote;
    }

    public String getSafetyNote() {
        return safetyNote;
    }

    public String getFoodNote() {
        return foodNote;
    }

    public String getClimateNote() {
        return climateNote;
    }

    public String getTransportNote() {
        return transportNote;
    }

    public String getTravelAlertNote() {
        return travelAlertNote;
    }

    @Override
    public String toString() {
        return id + " : " + name;
    }
}
