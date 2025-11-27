package com.eclectics.Garage.specificationExecutor;

import com.eclectics.Garage.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecificationExecutor {
    public static Specification<User> emailContains(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<User> firstnameContains(String firstname) {
        return (root, query, cb) -> {
            if (firstname == null || firstname.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("firstname")), "%" + firstname.toLowerCase() + "%");
        };
    }

    public static Specification<User> secondnameContains(String secondname) {
        return (root, query, cb) -> {
            if (secondname == null || secondname.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("secondname")), "%" + secondname.toLowerCase() + "%");
        };
    }
}
