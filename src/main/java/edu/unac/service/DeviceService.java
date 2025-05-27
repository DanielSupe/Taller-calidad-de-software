package edu.unac.service;

import edu.unac.domain.Device;
import edu.unac.domain.DeviceStatus;
import edu.unac.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
    }

    public Device registerDevice(Device device) {
        return null;
    }

    public List<Device> getAllDevices() {
        return null;
    }

    public Optional<Device> getDeviceById(Long id) {
        return null;
    }

    public Device updateDeviceStatus(Long id, DeviceStatus newStatus) {
        return null;
    }

    public void deleteDevice(Long id) {
    }
}
