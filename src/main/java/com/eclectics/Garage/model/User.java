    package com.eclectics.Garage.model;

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
        //    private String status;
        private boolean enabled = true;

        @Enumerated(EnumType.STRING)
        private Role role;

        @Transient
        private boolean detailsCompleted;

        public User(String email, String secondname, String firstname, String password, String phoneNumber, boolean enabled, Role role) {
            this.email = email;
            this.secondname = secondname;
            this.firstname = firstname;
            this.password = password;
            this.phoneNumber = phoneNumber;
            this.enabled = enabled;
            this.role = role;
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

        public boolean isDetailsCompleted(){
            return email != null && !email.isBlank()
                    && password != null && !password.isBlank();
//                    && firstname != null && !firstname.isBlank()
//                    && secondname != null && !secondname.isBlank()
//                    && phoneNumber != null && !phoneNumber.isBlank();
        }

        public User() {
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
    }

