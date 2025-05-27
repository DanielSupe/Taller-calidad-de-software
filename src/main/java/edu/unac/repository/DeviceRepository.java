package edu.unac.repository;

import edu.unac.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query("SELECT COUNT(l) > 0 FROM Loan l WHERE l.deviceId = :deviceId AND l.returned = false")
    boolean existsLoanByDeviceId(@Param("deviceId") Long deviceId);
}
