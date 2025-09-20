package com.quickcommerce.auth.repository;

import com.quickcommerce.auth.entity.Address;
import com.quickcommerce.auth.entity.AddressType;
import com.quickcommerce.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findAllByUser(User user);
    List<Address> findAllByUserAndType(User user, AddressType type);
}
