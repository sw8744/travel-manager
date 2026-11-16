
package kr.ac.konkuk;

public class Country {
    private int id;
    private String name;
    private String electricityNote;
    private String safetyNote;
    private String foodNote;
    private String climateNote;
    private String transportNote;
    private String travelAlertNote;

    public Country(int id, String name, String electricityNote,
                   String safetyNote, String foodNote,
                   String climateNote, String transportNote,
                   String travelAlertNote) {
        this.id = id;
        this.name = name;
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

    public void showCautions(City city) {
        System.out.println("\n===== " + name + " / " + city.getName() + " 여행 유의사항 =====");

        showNote("전기", electricityNote, city.getElectricityNote());
        showNote("치안", safetyNote, city.getSafetyNote());
        showNote("식사", foodNote, city.getFoodNote());
        showNote("기후", climateNote, city.getClimateNote());
        showNote("교통", transportNote, city.getTransportNote());
        showNote("여행경보", travelAlertNote, city.getTravelAlertNote());

        System.out.println("\n======================================");
    }

    private void showNote(String title, String countryNote, String cityNote) {
        boolean hasCountry = countryNote != null && !countryNote.isBlank();
        boolean hasCity = cityNote != null && !cityNote.isBlank();

        if (!hasCountry && !hasCity) {
            return;
        }

        System.out.println("\n[" + title + "]");

        if (hasCountry) {
            System.out.println("국가 : " + countryNote);
        }

        if (hasCity) {
            if (!hasCountry || !countryNote.trim().equals(cityNote.trim())) {
                System.out.println("도시 : " + cityNote);
            }
        }
    }

    @Override
    public String toString() {
        return id + " : " + name;
    }
}
