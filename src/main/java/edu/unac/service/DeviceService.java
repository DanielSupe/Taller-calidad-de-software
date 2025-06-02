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
        this.deviceRepository = deviceRepository;
    }

    public Device registerDevice(Device device) {
        if (device.getName() == null || device.getName().length() < 3) {
            throw new IllegalArgumentException("Device name must be at least 3 characters long");
        }

        device.setStatus(DeviceStatus.AVAILABLE);
        device.setAddedDate(System.currentTimeMillis());

        return deviceRepository.save(device);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Optional<Device> getDeviceById(Long id) {
        return deviceRepository.findById(id);
    }

    public Device updateDeviceStatus(Long id, DeviceStatus newStatus) {
        Device device = deviceRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Device not found"));

        device.setStatus(newStatus);

        return deviceRepository.save(device);
    }

    public void deleteDevice(Long id) {
        if (deviceRepository.existsLoanByDeviceId(id)) {
            throw new IllegalStateException("Cannot delete device with loan history");
        }

        deviceRepository.deleteById(id);
    }
}
