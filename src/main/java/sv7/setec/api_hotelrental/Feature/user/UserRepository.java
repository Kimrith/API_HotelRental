package sv7.setec.api_hotelrental.Feature.user;

import sv7.setec.api_hotelrental.Feature.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find by unique fields
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // Support single-field login using username or email (maps to UserLoginDto.identifier)
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByIdentifier(@Param("identifier") String identifier);

    // Uniqueness validation checks for registration
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}