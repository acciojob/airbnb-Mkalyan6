package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.*;

public class HotelManagementRepository {

    HashMap<String,Hotel>HotelDB=new HashMap<>();
    HashMap<Integer,User>UserDB=new HashMap<>();
    HashMap<String,Booking>BookingDB=new HashMap<>();
    HashMap<Integer,Integer>UserBooking=new HashMap<>();
    public String addHotel(Hotel hotel) {
        String HotelName=hotel.getHotelName();
        if(HotelName==null||hotel==null){
            return "FAILURE";
        }
        if(HotelDB.containsKey(HotelName)){
            return "FAILURE";
        }
        HotelDB.put(HotelName,hotel);
        return "SUCCESS";
    }

    public Integer addUser(User user) {
        int aadhar=user.getaadharCardNo();
        UserDB.put(aadhar,user);
        return aadhar;

    }

    public String getHotelWithMostFacilities() {
        String finalName=null;
         class pair{
            final String hotelName;
            final int CountOfFacilities;
            pair(String hotelname,int CountOfFacilites){
                this.hotelName=hotelname;
                this.CountOfFacilities=CountOfFacilites;
            }
        }
        PriorityQueue<pair>pq=new PriorityQueue<pair>((a,b)->{
            return b.CountOfFacilities-a.CountOfFacilities;
        });

        int FacilityListCount=0;
        for(Map.Entry<String,Hotel>mapElement:HotelDB.entrySet()){
            String H_Name= mapElement.getKey();
            Hotel H_Object=mapElement.getValue();
            int CountOfFacilities= H_Object.getFacilities().size();
            pq.add(new pair(H_Name,CountOfFacilities));
        }

        if(pq.size()==0)return "";
        if(pq.size()==1) {
            if(pq.peek().CountOfFacilities==0)
                return "";
            return pq.remove().hotelName;
        }
        while(pq.size()>1){
            pair temp1=pq.remove();
            pair temp2=pq.remove();
            if(temp1.CountOfFacilities!=temp2.CountOfFacilities) {return temp1.hotelName;}
            if(temp1.CountOfFacilities==0)return"";
            String str1=temp1.hotelName;
            String str2=temp2.hotelName;
            if(str1.compareTo(str2)>0)pq.add(temp2);
            else pq.add(temp1);
        }
        return pq.remove().hotelName;
    }

    public int bookARoom(Booking booking) {
        // Generate Booking Id of our own
        // Calculate Total amount based on price and availability of rooms
        UUID Id= UUID.randomUUID();
        String BookingId= String.valueOf(Id);
        if(HotelDB.containsKey(booking.getHotelName())){
            String BookedHotelName=booking.getHotelName();
            Hotel hoteldetails=HotelDB.get(BookedHotelName);
               int avaliableRooms=hoteldetails.getAvailableRooms();
               int requiredRooms=booking.getNoOfRooms();
               if(requiredRooms<=avaliableRooms){
                   avaliableRooms=avaliableRooms-requiredRooms;
                   hoteldetails.setAvailableRooms(avaliableRooms);
                   int pricePerRoom=hoteldetails.getPricePerNight();
                   int Totalcost=requiredRooms*pricePerRoom;
                   booking.setAmountToBePaid(Totalcost);

                   BookingDB.put(BookingId,booking);

                   // Now count the bookings made by this aadhar card person and put in hashmap
                   int aadhar=booking.getBookingAadharCard();
                   String userName=booking.getBookingPersonName();
                   int CountOfBookings=UserBooking.getOrDefault(aadhar,0)+1;
                   UserBooking.put(aadhar,CountOfBookings);

                   // Add this user to user data base , if he is new user of the application
                   if(!UserDB.containsKey(aadhar)){
                       UserDB.put(aadhar,new User(aadhar,userName,0));
                   }
                   return Totalcost;
               }
        }
                  return -1;
    }

    public int getBookings(Integer aadharCard) {
        // if adharcard is present then, have to search for booking details
        if(UserDB.containsKey(aadharCard)){
            if(UserBooking.containsKey(aadharCard)){
                return UserBooking.get(aadharCard);
            }

        }
        return 0;
    }

    public Hotel updateFacilites(List<Facility> newFacilities, String hotelName) {
        if(HotelDB.containsKey(hotelName)){
            List<Facility>ListOfFacilities=HotelDB.get(hotelName).getFacilities();
            for(Facility newFacility:newFacilities){
                if(!ListOfFacilities.contains(newFacility)){
                    // this facility is new to this hotel,so add that to list
                    ListOfFacilities.add(newFacility);
                }
            }
            Hotel hotelObject=HotelDB.get(hotelName);
            hotelObject.setFacilities(ListOfFacilities);
            HotelDB.put(hotelName,hotelObject);
            return hotelObject;
        }
        return null;
    }
}

