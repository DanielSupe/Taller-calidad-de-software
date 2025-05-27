package edu.unac.service;

import edu.unac.domain.Device;
import edu.unac.domain.DeviceStatus;
import edu.unac.domain.Loan;
import edu.unac.repository.DeviceRepository;
import edu.unac.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoanService {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private DeviceRepository deviceRepository;

    public LoanService(LoanRepository loanRepository, DeviceRepository deviceRepository) {
        this.loanRepository = loanRepository;
        this.deviceRepository = deviceRepository;
    }

    public Loan registerLoan(Loan loan) {
        Device device = deviceRepository.findById(loan.getDeviceId()).orElseThrow(() ->
                new IllegalArgumentException("Device not found"));

        if (device.getStatus() != DeviceStatus.AVAILABLE) {
            throw new IllegalStateException("Device is not available for loan");
        }

        loan.setStartDate(System.currentTimeMillis());
        loan.setReturned(false);

        device.setStatus(DeviceStatus.LOANED);
        deviceRepository.save(device);

        return loanRepository.save(loan);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public Loan markAsReturned(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() ->
                new IllegalArgumentException("Loan not found"));

        if (loan.isReturned()) {
            throw new IllegalStateException("Loan is already marked as returned");
        }

        loan.setReturned(true);
        loan.setEndDate(System.currentTimeMillis());

        Device device = deviceRepository.findById(loan.getDeviceId()).orElseThrow(() ->
                new IllegalArgumentException("Device not found"));
        device.setStatus(DeviceStatus.AVAILABLE);
        deviceRepository.save(device);

        return loanRepository.save(loan);
    }

    public List<Loan> getLoansByDeviceId(Long deviceId) {
        return loanRepository.findByDeviceId(deviceId);
    }
}
