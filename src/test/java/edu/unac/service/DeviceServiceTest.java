package edu.unac.service;

import edu.unac.domain.Device;
import edu.unac.domain.DeviceStatus;
import edu.unac.repository.DeviceRepository;
import net.bytebuddy.implementation.bytecode.Division;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class DeviceServiceTest {
    private DeviceRepository deviceRepository;
    private DeviceService deviceService;

    @BeforeEach
    void setUp() {
        deviceRepository = mock(DeviceRepository.class);
        deviceService = new DeviceService(deviceRepository);
    }

    @Test
    void registerDevice_validDevice_shouldSetStatusAndSave() {
        Device input = new Device();
        input.setName("Laptop");
        input.setType("Electronics");
        input.setLocation("Shelf A");

        Device saved = new Device();
        saved.setId(1L);
        saved.setName("Laptop");
        saved.setType("Electronics");
        saved.setLocation("Shelf A");
        saved.setStatus(DeviceStatus.AVAILABLE);
        saved.setAddedDate(123456L);

        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> {
            Device d = invocation.getArgument(0);
            d.setId(1L);
            return d;
        });

        Device result = deviceService.registerDevice(input);

        assertEquals(DeviceStatus.AVAILABLE, result.getStatus());
        assertNotNull(result.getAddedDate());
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void registerDevice_invalidName_shouldThrowException() {
        Device input = new Device();
        input.setName("AB");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                deviceService.registerDevice(input));
        assertEquals("Device name must be at least 3 characters long", exception.getMessage());
        verify(deviceRepository, never()).save(any());
    }

    @Test
    void getAllDevices_shouldReturnList() {
        List<Device> devices = Arrays.asList(new Device(), new Device());
        when(deviceRepository.findAll()).thenReturn(devices);

        List<Device> result = deviceService.getAllDevices();

        assertEquals(2, result.size());
        verify(deviceRepository).findAll();
    }

    @Test
    void getDeviceById_shouldReturnDevice() {
        Device device = new Device();
        device.setId(1L);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        Optional<Device> result = deviceService.getDeviceById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void updateDeviceStatus_existingDevice_shouldUpdateStatus() {
        Device device = new Device();
        device.setId(1L);
        device.setStatus(DeviceStatus.AVAILABLE);

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenReturn(device);

        Device updated = deviceService.updateDeviceStatus(1L, DeviceStatus.MAINTENANCE);

        assertEquals(DeviceStatus.MAINTENANCE, updated.getStatus());
        verify(deviceRepository).save(device);
    }

    @Test
    void updateDeviceStatus_deviceNotFound_shouldThrowException() {
        when(deviceRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                deviceService.updateDeviceStatus(99L, DeviceStatus.MAINTENANCE));
        assertEquals("Device not found", exception.getMessage());
    }

    @Test
    void updateDeviceStatus_deviceNotFound_shouldThrowExceptionNoName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                deviceService.registerDevice(new Device()));
        assertEquals("Device name must be at least 3 characters long", exception.getMessage());
    }

    @Test
    void deleteDevice_withLoanHistory_shouldThrowException() {
        when(deviceRepository.existsLoanByDeviceId(1L)).thenReturn(true);

        Exception exception = assertThrows(IllegalStateException.class, () ->
                deviceService.deleteDevice(1L));
        assertEquals("Cannot delete device with loan history", exception.getMessage());
        verify(deviceRepository, never()).deleteById(any());
    }

    @Test
    void deleteDevice_noLoanHistory_shouldDeleteDevice() {
        when(deviceRepository.existsLoanByDeviceId(1L)).thenReturn(false);

        deviceService.deleteDevice(1L);

        verify(deviceRepository).deleteById(1L);
    }
}