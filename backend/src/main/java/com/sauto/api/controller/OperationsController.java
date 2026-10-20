package com.sauto.api.controller;

import com.sauto.api.dto.Requests.DocumentRequest;
import com.sauto.api.dto.Requests.DriverRequest;
import com.sauto.api.dto.Requests.InvoiceRequest;
import com.sauto.api.dto.Requests.PaymentRequest;
import com.sauto.api.dto.Requests.StatusRequest;
import com.sauto.api.dto.Requests.VehicleRequest;
import com.sauto.api.model.Driver;
import com.sauto.api.model.FleetDocument;
import com.sauto.api.model.InvoiceRecord;
import com.sauto.api.model.PaymentRecord;
import com.sauto.api.model.Vehicle;
import com.sauto.api.repository.DriverRepository;
import com.sauto.api.repository.FleetDocumentRepository;
import com.sauto.api.repository.InvoiceRepository;
import com.sauto.api.repository.PaymentRepository;
import com.sauto.api.repository.VehicleRepository;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class OperationsController {
    private final VehicleRepository vehicles;
    private final DriverRepository drivers;
    private final FleetDocumentRepository documents;
    private final PaymentRepository payments;
    private final InvoiceRepository invoices;

    public OperationsController(VehicleRepository vehicles, DriverRepository drivers,
                                FleetDocumentRepository documents, PaymentRepository payments,
                                InvoiceRepository invoices) {
        this.vehicles = vehicles;
        this.drivers = drivers;
        this.documents = documents;
        this.payments = payments;
        this.invoices = invoices;
    }

    @GetMapping("/vehicles")
    public List<Vehicle> vehicles(Principal principal) { return vehicles.findAllByOwnerEmailOrderByIdDesc(principal.getName()); }

    @PostMapping("/vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicle addVehicle(@Valid @RequestBody VehicleRequest input, Principal principal) {
        Vehicle vehicle = new Vehicle(); apply(input, vehicle); vehicle.assignOwner(principal.getName()); return vehicles.save(vehicle);
    }

    @PutMapping("/vehicles/{id}")
    public Vehicle updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleRequest input, Principal principal) {
        Vehicle vehicle = owned(vehicles.findByIdAndOwnerEmail(id, principal.getName())); apply(input, vehicle); return vehicles.save(vehicle);
    }

    @PatchMapping("/vehicles/{id}/status")
    public Vehicle updateVehicleStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest input, Principal principal) {
        Vehicle vehicle = owned(vehicles.findByIdAndOwnerEmail(id, principal.getName())); vehicle.status = input.status(); return vehicles.save(vehicle);
    }

    @DeleteMapping("/vehicles/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVehicle(@PathVariable Long id, Principal principal) { vehicles.delete(owned(vehicles.findByIdAndOwnerEmail(id, principal.getName()))); }

    @GetMapping("/drivers")
    public List<Driver> drivers(Principal principal) { return drivers.findAllByOwnerEmailOrderByIdDesc(principal.getName()); }

    @PostMapping("/drivers")
    @ResponseStatus(HttpStatus.CREATED)
    public Driver addDriver(@Valid @RequestBody DriverRequest input, Principal principal) {
        Driver driver = new Driver(); apply(input, driver); driver.assignOwner(principal.getName()); return drivers.save(driver);
    }

    @PutMapping("/drivers/{id}")
    public Driver updateDriver(@PathVariable Long id, @Valid @RequestBody DriverRequest input, Principal principal) {
        Driver driver = owned(drivers.findByIdAndOwnerEmail(id, principal.getName())); apply(input, driver); return drivers.save(driver);
    }

    @DeleteMapping("/drivers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDriver(@PathVariable Long id, Principal principal) { drivers.delete(owned(drivers.findByIdAndOwnerEmail(id, principal.getName()))); }

    @GetMapping("/documents")
    public List<FleetDocument> documents(Principal principal) { return documents.findAllByOwnerEmailOrderByIdDesc(principal.getName()); }

    @PostMapping("/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public FleetDocument addDocument(@Valid @RequestBody DocumentRequest input, Principal principal) {
        FleetDocument document = new FleetDocument(); apply(input, document); document.assignOwner(principal.getName()); return documents.save(document);
    }

    @GetMapping("/payments")
    public List<PaymentRecord> payments(Principal principal) { return payments.findAllByOwnerEmailOrderByIdDesc(principal.getName()); }

    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentRecord addPayment(@Valid @RequestBody PaymentRequest input, Principal principal) {
        PaymentRecord payment = new PaymentRecord(); apply(input, payment); payment.assignOwner(principal.getName()); return payments.save(payment);
    }

    @PatchMapping("/payments/{id}/paid")
    public PaymentRecord markPaymentPaid(@PathVariable Long id, Principal principal) {
        PaymentRecord payment = owned(payments.findByIdAndOwnerEmail(id, principal.getName()));
        payment.status = "Paid"; payment.paidOn = LocalDate.now(); return payments.save(payment);
    }

    @GetMapping("/invoices")
    public List<InvoiceRecord> invoices(Principal principal) { return invoices.findAllByOwnerEmailOrderByIdDesc(principal.getName()); }

    @PostMapping("/invoices")
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceRecord addInvoice(@Valid @RequestBody InvoiceRequest input, Principal principal) {
        InvoiceRecord invoice = new InvoiceRecord(); apply(input, invoice); invoice.assignOwner(principal.getName()); return invoices.save(invoice);
    }

    @PatchMapping("/invoices/{id}/paid")
    public InvoiceRecord markInvoicePaid(@PathVariable Long id, Principal principal) {
        InvoiceRecord invoice = owned(invoices.findByIdAndOwnerEmail(id, principal.getName())); invoice.status = "Paid"; return invoices.save(invoice);
    }

    @GetMapping("/reports/summary")
    public Map<String, Object> reportSummary(Principal principal) {
        List<Vehicle> vehicleList = vehicles.findAllByOwnerEmailOrderByIdDesc(principal.getName());
        List<Driver> driverList = drivers.findAllByOwnerEmailOrderByIdDesc(principal.getName());
        List<FleetDocument> documentList = documents.findAllByOwnerEmailOrderByIdDesc(principal.getName());
        List<PaymentRecord> paymentList = payments.findAllByOwnerEmailOrderByIdDesc(principal.getName());
        List<InvoiceRecord> invoiceList = invoices.findAllByOwnerEmailOrderByIdDesc(principal.getName());
        long dueDocuments = documentList.stream().filter(item -> item.expiresOn != null && !item.expiresOn.isBefore(LocalDate.now()) && !item.expiresOn.isAfter(LocalDate.now().plusDays(30))).count();
        long pendingTotal = paymentList.stream().filter(item -> !"Paid".equalsIgnoreCase(item.status)).mapToLong(item -> item.amount == null ? 0 : item.amount).sum();
        long outstanding = invoiceList.stream().filter(item -> !"Paid".equalsIgnoreCase(item.status)).mapToLong(item -> item.amount == null ? 0 : item.amount).sum();
        long collected = invoiceList.stream().filter(item -> "Paid".equalsIgnoreCase(item.status)).mapToLong(item -> item.amount == null ? 0 : item.amount).sum();
        Map<String, Object> summary = new HashMap<>();
        summary.put("vehicleCount", vehicleList.size());
        summary.put("vehiclesOnRoute", vehicleList.stream().filter(item -> "On route".equalsIgnoreCase(item.status)).count());
        summary.put("activeDrivers", driverList.stream().filter(item -> !Boolean.FALSE.equals(item.active)).count());
        summary.put("documentsDueWithin30Days", dueDocuments);
        summary.put("pendingPayments", pendingTotal);
        summary.put("invoiceOutstanding", outstanding);
        summary.put("invoiceCollected", collected);
        return summary;
    }

    private static void apply(VehicleRequest input, Vehicle target) {
        target.name = input.name(); target.plate = input.plate(); target.category = input.category(); target.status = input.status();
        target.driver = input.driver(); target.nextService = input.nextService(); target.mileage = input.mileage(); target.utilization = input.utilization(); target.imageData = input.imageData();
    }

    private static void apply(DriverRequest input, Driver target) {
        target.name = input.name(); target.phone = input.phone(); target.email = input.email(); target.licenseExpiry = input.licenseExpiry();
        target.experience = input.experience(); target.rating = input.rating(); target.active = input.active() == null || input.active();
    }

    private static void apply(DocumentRequest input, FleetDocument target) {
        target.type = input.type(); target.entity = input.entity(); target.title = input.title(); target.reference = input.reference();
        target.expiresOn = input.expiresOn(); target.status = input.status() == null ? "Upcoming" : input.status();
    }

    private static void apply(PaymentRequest input, PaymentRecord target) {
        target.reference = input.reference(); target.partner = input.partner(); target.description = input.description(); target.amount = input.amount();
        target.dueOn = input.dueOn(); target.paidOn = input.paidOn(); target.status = input.status() == null ? "Pending" : input.status(); target.method = input.method();
    }

    private static void apply(InvoiceRequest input, InvoiceRecord target) {
        target.reference = input.reference(); target.customer = input.customer(); target.description = input.description(); target.amount = input.amount();
        target.issuedOn = input.issuedOn(); target.dueOn = input.dueOn(); target.status = input.status() == null ? "Sent" : input.status();
    }

    private static <T> T owned(java.util.Optional<T> record) {
        return record.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "This record was not found."));
    }
}
