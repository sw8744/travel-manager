package kr.ac.konkuk;

public class Flight {
    public String flightNumber;
    public String departure; // FIXME: String -> Country or City로 변경 필요
    public String arrival; // FIXME: String -> Country or City로 변경 필요
    public String departureTime;
    public String arrivalTime;

    public Flight(String flightNumber, String departure, String arrival, String departureTime, String arrivalTime) {
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.arrival = arrival;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }
}
