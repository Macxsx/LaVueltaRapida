package com.example.demo.dto;

public class MpPayerRequest {

    private String name;
    private String surname;
    private String email;
    private MpPhoneRequest phone;
    private MpAddressRequest address;

    public MpPayerRequest() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public MpPhoneRequest getPhone() { return phone; }
    public void setPhone(MpPhoneRequest phone) { this.phone = phone; }

    public MpAddressRequest getAddress() { return address; }
    public void setAddress(MpAddressRequest address) { this.address = address; }

    public static class MpPhoneRequest {
        private String area_code;
        private String number;

        public String getArea_code() { return area_code; }
        public void setArea_code(String area_code) { this.area_code = area_code; }

        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number; }
    }

    public static class MpAddressRequest {
        private String street_name;

        public String getStreet_name() { return street_name; }
        public void setStreet_name(String street_name) { this.street_name = street_name; }
    }
}
