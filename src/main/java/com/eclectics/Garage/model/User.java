    package com.eclectics.Garage.model;

    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;

    @Entity
    @Table(name = "users")
    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private String email;

        @Column(nullable = false)
        private String firstname;

        @Column(nullable = false)
        private String secondname;

        @Column(nullable = false)
        private String password;

        @Column(nullable = false)
        private String phoneNumber;

        private boolean enabled = false;

        @Enumerated(EnumType.STRING)
        private Role role;

        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
        @JsonManagedReference
        private Mechanic mechanic;

        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
        @JsonManagedReference
        private CarOwner carOwner;

        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
        @JsonManagedReference
        private Garage garage;

        public User(String email, String secondname,Mechanic mechanic,CarOwner carOwner,Garage garage, String firstname, String password, String phoneNumber, boolean enabled, Role role) {
            this.email = email;
            this.secondname = secondname;
            this.firstname = firstname;
            this.password = password;
            this.phoneNumber = phoneNumber;
            this.enabled = enabled;
            this.role = role;
            this.mechanic = mechanic;
            this.carOwner = carOwner;
            this.garage = garage;
        }

        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstname() {
            return firstname;
        }
        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getSecondname() {
            return secondname;
        }
        public void setSecondname(String secondname) {
            this.secondname = secondname;
        }

        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }
        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public boolean isEnabled() {
            return enabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Role getRole() {
            return role;
        }
        public void setRole(Role role) {
            this.role = role;
        }

        public Mechanic getMechanic() {return mechanic;}
        public void setMechanic(Mechanic mechanic) {this.mechanic = mechanic;}

        public Garage getGarage() {return garage;}
        public void setGarage(Garage garage) {this.garage = garage;}

        public User() {
        }

        @Transient
        public boolean isDetailsCompleted() {
            switch (this.role) {
                case MECHANIC:
                    return mechanic != null && mechanic.isComplete();
                case CAR_OWNER:
                    return carOwner != null && carOwner.isComplete();
                case GARAGE_ADMIN:
                    return garage != null && garage.isComplete();
                default:
                    return false;
            }
        }
    }

