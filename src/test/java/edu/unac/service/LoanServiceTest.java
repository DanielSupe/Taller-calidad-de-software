package edu.unac.service;

import edu.unac.domain.Device;
import edu.unac.domain.DeviceStatus;
import edu.unac.domain.Loan;
import edu.unac.repository.DeviceRepository;
import edu.unac.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {

    private LoanRepository loanRepo;
    private DeviceRepository deviceRepo;
    private LoanService service;

    @BeforeEach
    void setUp() {
        loanRepo = mock(LoanRepository.class);
        deviceRepo = mock(DeviceRepository.class);
        service = new LoanService(loanRepo, deviceRepo);
    }

    @Test
    void createLoan_ok() {
        Loan loan = new Loan();
        loan.setDeviceId(1L);

        Device device = new Device();
        device.setId(1L);
        device.setStatus(DeviceStatus.AVAILABLE);

        when(deviceRepo.findById(1L)).thenReturn(Optional.of(device));
        when(deviceRepo.save(any(Device.class))).thenReturn(device);
        when(loanRepo.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = service.registerLoan(loan);

        assertNotNull(result.getStartDate());
        assertFalse(result.isReturned());
        assertEquals(DeviceStatus.LOANED, device.getStatus());
        verify(loanRepo).save(loan);
        verify(deviceRepo).save(device);
    }

    @Test
    void createLoan_deviceNotFound() {
        Loan loan = new Loan();
        loan.setDeviceId(1L);

        when(deviceRepo.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.registerLoan(loan));
        assertEquals("Device not found", ex.getMessage());
        verify(loanRepo, never()).save(any());
    }

    @Test
    void createLoan_deviceBusy() {
        Loan loan = new Loan();
        loan.setDeviceId(1L);

        Device device = new Device();
        device.setId(1L);
        device.setStatus(DeviceStatus.LOANED);

        when(deviceRepo.findById(1L)).thenReturn(Optional.of(device));

        Exception ex = assertThrows(IllegalStateException.class, () -> service.registerLoan(loan));
        assertEquals("Device is not available for loan", ex.getMessage());
        verify(loanRepo, never()).save(any());
    }

    @Test
    void getAllLoans() {
        Loan loan1 = new Loan();
        Loan loan2 = new Loan();

        when(loanRepo.findAll()).thenReturn(Arrays.asList(loan1, loan2));

        List<Loan> result = service.getAllLoans();

        assertEquals(2, result.size());
        verify(loanRepo).findAll();
    }

    @Test
    void getLoan_found() {
        Loan loan = new Loan();
        loan.setId(10L);

        when(loanRepo.findById(10L)).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getLoanById(10L);

        assertTrue(result.isPresent());
        assertEquals(loan, result.get());
        verify(loanRepo).findById(10L);
    }

    @Test
    void getLoan_notFound() {
        when(loanRepo.findById(10L)).thenReturn(Optional.empty());

        Optional<Loan> result = service.getLoanById(10L);

        assertTrue(result.isEmpty());
        verify(loanRepo).findById(10L);
    }

    @Test
    void returnLoan_ok() {
        Loan loan = new Loan();
        loan.setId(5L);
        loan.setDeviceId(1L);
        loan.setReturned(false);

        Device device = new Device();
        device.setId(1L);
        device.setStatus(DeviceStatus.LOANED);

        when(loanRepo.findById(5L)).thenReturn(Optional.of(loan));
        when(deviceRepo.findById(1L)).thenReturn(Optional.of(device));
        when(loanRepo.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = service.markAsReturned(5L);

        assertTrue(result.isReturned());
        assertNotNull(result.getEndDate());
        assertEquals(DeviceStatus.AVAILABLE, device.getStatus());
        verify(loanRepo).save(loan);
        verify(deviceRepo).save(device);
    }

    @Test
    void returnLoan_loanNotFound() {
        when(loanRepo.findById(5L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.markAsReturned(5L));
        assertEquals("Loan not found", ex.getMessage());
        verify(loanRepo, never()).save(any());
    }

    @Test
    void returnLoan_alreadyReturned() {
        Loan loan = new Loan();
        loan.setId(5L);
        loan.setReturned(true);

        when(loanRepo.findById(5L)).thenReturn(Optional.of(loan));

        Exception ex = assertThrows(IllegalStateException.class, () -> service.markAsReturned(5L));
        assertEquals("Loan is already marked as returned", ex.getMessage());
        verify(loanRepo, never()).save(any());
    }

    @Test
    void getLoansByDeviceId_ok() {
        Loan loan1 = new Loan();
        Loan loan2 = new Loan();

        when(loanRepo.findByDeviceId(99L)).thenReturn(Arrays.asList(loan1, loan2));

        List<Loan> result = service.getLoansByDeviceId(99L);

        assertEquals(2, result.size());
        verify(loanRepo).findByDeviceId(99L);
    }

    @Test
    void returnLoan_deviceNotFound() {
        Loan loan = new Loan();
        loan.setId(5L);
        loan.setDeviceId(1L);
        loan.setReturned(false);

        when(loanRepo.findById(5L)).thenReturn(Optional.of(loan));
        when(deviceRepo.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.markAsReturned(5L));
        assertEquals("Device not found", ex.getMessage());
        verify(loanRepo, never()).save(any());
    }
}
