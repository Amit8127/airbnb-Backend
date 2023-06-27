package com.driver.repositories;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class HotelManagementRepository {

    private Map<String, Hotel> hotelMap = new HashMap<>();
    private Map<Integer, User> userMap = new HashMap<>();

    private Map<String, Booking> bookingMap = new HashMap<>();
    public String addHotel(Hotel hotel) {
        //You need to add an hotel to the database
        //incase the hotelName is null or the hotel Object is null return an empty a FAILURE
        //Incase somebody is trying to add the duplicate hotelName return FAILURE
        //in all other cases return SUCCESS after successfully adding the hotel to the hotelDb.
        if(hotel == null || hotel.getHotelName() == null) {
            return "FAILURE";
        }
        if(hotelMap.containsKey(hotel.getHotelName())) {
            return  "FAILURE";
        }
        hotelMap.put(hotel.getHotelName(), hotel);
        return "SUCCESS";
    }

    public Integer addUser(User user) {
        //You need to add a User Object to the database
        //Assume that user will always be a valid user and return the aadharCardNo of the user
        userMap.put(user.getaadharCardNo(), user);
        return user.getaadharCardNo();
    }

    public String getHotelWithMostFacilities() {
        //Out of all the hotels we have added so far, we need to find the hotelName with most no of facilities
        //Incase there is a tie return the lexicographically smaller hotelName
        //Incase there is not even a single hotel with atleast 1 facility return "" (empty string)
        String name = "";
        int maxF = Integer.MIN_VALUE;
        for(Hotel hotel : hotelMap.values()) {
            if(maxF < hotel.getFacilities().size()) {
                maxF = hotel.getFacilities().size();
                name = hotel.getHotelName();
            } else if (maxF == hotel.getFacilities().size() && name.compareTo(hotel.getHotelName()) > 0) {
                name = hotel.getHotelName();
            }
        }
        return name;
    }

    public int bookARoom(Booking booking) {
        //The booking object coming from postman will have all the attributes except bookingId and amountToBePaid;
        //Have bookingId as a random UUID generated String
        //save the booking Entity and keep the bookingId as a primary key
        //Calculate the total amount paid by the person based on no. of rooms booked and price of the room per night.
        //If there arent enough rooms available in the hotel that we are trying to book return -1
        //in other case return total amount paid
        String bookingId = UUID.randomUUID().toString();
        booking.setBookingId(bookingId);
        Hotel hotel = hotelMap.get(booking.getHotelName());
        if(booking.getNoOfRooms() > hotel.getAvailableRooms()) {
            return -1;
        }
        int totalPrice = booking.getNoOfRooms() * hotel.getPricePerNight();
        booking.setAmountToBePaid(totalPrice);
        hotel.setAvailableRooms(hotel.getAvailableRooms() - booking.getNoOfRooms());
        bookingMap.put(bookingId, booking);
        hotelMap.put(hotel.getHotelName(), hotel);
        return totalPrice;
    }

    public int getBookings(Integer aadharCard) {
        int bookings = 0;
        for(Booking booking : bookingMap.values()) {
            String userName = booking.getBookingPersonName();
            if(userMap.get(userName).getaadharCardNo() == aadharCard) {
                bookings++;
            }
        }
        return bookings;
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {
        //We are having a new facilites that a hotel is planning to bring.
        //If the hotel is already having that facility ignore that facility otherwise add that facility in the hotelDb
        //return the final updated List of facilities and also update that in your hotelDb
        //Note that newFacilities can also have duplicate facilities possible
        Hotel hotel = hotelMap.get(hotelName);
        for(Facility facility : newFacilities) {
            if (!hotel.getFacilities().contains(facility)) {
                hotel.getFacilities().add(facility);
            }
        }
        hotelMap.put(hotel.getHotelName(), hotel);
        return hotel;
    }
}
