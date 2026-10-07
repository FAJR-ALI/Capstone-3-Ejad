package com.example.ejadwebapplication.Config;

import com.example.ejadwebapplication.Enums.LocationType;
import com.example.ejadwebapplication.Enums.ReportStatus;
import com.example.ejadwebapplication.Enums.ReportType;
import com.example.ejadwebapplication.Model.*;
import com.example.ejadwebapplication.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.util.Set;
import java.util.List;

// نعبي بيانات تجريبية أول مرة يشتغل فيها المشروع
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final LocationRepository locationRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final CategoryRepository categoryRepository;
    private final ReportRepository reportRepository;

    @Override
    public void run(String... args) {
        // كل جزء يتحقق لحاله، عشان لو الأماكن موجودة والتصنيفات لا، تنضاف التصنيفات
        seedCategories();
        seedLocationsAndAccounts();
        seedReports();
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) {
            return;
        }
        categoryRepository.saveAll(List.of(
                createCategory("Electronics", "Phones, laptops, headphones, chargers"),
                createCategory("Wallets", "Wallets and card holders"),
                createCategory("Bags", "Backpacks, handbags, luggage"),
                createCategory("Keys", "House keys and car keys"),
                createCategory("Documents", "IDs, passports, cards and papers"),
                createCategory("Clothes", "Clothes, abayas, shoes, accessories"),
                createCategory("Other", "Anything else")
        ));
    }

    private void seedLocationsAndAccounts() {
        if (locationRepository.count() > 0) {
            return;
        }

        Location airport = createLocation("King Khalid International Airport",
                "Lost and found desk, Terminal 1", "Riyadh", LocationType.AIRPORT);
        Location mall = createLocation("Al-Hamra mall",
                "Customer service desk, ground floor", "Riyadh", LocationType.MALL);
        Location metro = createLocation("KAFD Metro Station",
                "Station security office", "Riyadh", LocationType.METRO);
        Location university = createLocation("King Saud University",
                "Campus security office", "Riyadh", LocationType.UNIVERSITY);
        locationRepository.saveAll(List.of(airport, mall, metro, university));

        Admin admin = fillAccount(new Admin(), "System Admin", "admin",
                "eazmirara+admin@gmail.com", "Admin1234", "0543230737");
        adminRepository.save(admin);

        User sara = fillAccount(new User(), "Sara Alqahtani", "sara",
                "eazmirara+sara@gmail.com", "Sara1234", "0543230737");
        User fahad = fillAccount(new User(), "Fahad Alotaibi", "fahad",
                "eazmirara+fahad@gmail.com", "Fahad1234", "0556544660");
        userRepository.saveAll(List.of(sara, fahad));

        Staff khalid = fillAccount(new Staff(), "Khalid Alharbi", "khalid.staff",
                "eazmirara+khalid.staff@gmail.com", "Khalid1234", "0549931017");
        khalid.setLocation(airport);
        khalid.setIsVerified(true);

        // غير موثّقة عشان تجربون endpoint التوثيق
        Staff noura = fillAccount(new Staff(), "Noura Alshehri", "noura.staff",
                "eazmirara+noura.staff@gmail.com", "Noura1234", "0543230737");
        noura.setLocation(mall);
        noura.setIsVerified(false);

        staffRepository.saveAll(List.of(khalid, noura));
    }

    private void seedReports() {
        if (reportRepository.count() > 0) {
            return;
        }

        List<Category> categories = categoryRepository.findAll();
        List<Location> locations = locationRepository.findAll();
        List<User> users = userRepository.findAll();
        List<Staff> staff = staffRepository.findAll();

        Report report1 = new Report();
        report1.setType(ReportType.LOST);
        report1.setTitle("Lost iPhone 15");
        report1.setDescription("Black iPhone 15 lost near the airport terminal.");
        report1.setColor("Black");
        report1.setBrand("Apple");
        report1.setImageUrl("https://example.com/iphone15.jpg");
        report1.setItemDate(LocalDate.now().minusDays(5));
        report1.setStatus(ReportStatus.OPEN);
        report1.setUser(users.get(0));
        report1.setStaff(staff.get(0));
        report1.setCategory(categories.get(0));
        report1.setLocations(Set.of(locations.get(0)));

        Report report2 = new Report();
        report2.setType(ReportType.FOUND);
        report2.setTitle("Found Black Wallet");
        report2.setDescription("Black leather wallet found at the airport.");
        report2.setColor("Black");
        report2.setBrand("Gucci");
        report2.setImageUrl("https://example.com/wallet.jpg");
        report2.setItemDate(LocalDate.now().minusDays(3));
        report2.setStatus(ReportStatus.MATCHED);
        report2.setUser(users.get(1));
        report2.setStaff(staff.get(0));
        report2.setCategory(categories.get(1));
        report2.setLocations(Set.of(locations.get(0)));

        Report report3 = new Report();
        report3.setType(ReportType.LOST);
        report3.setTitle("Lost Blue Backpack");
        report3.setDescription("Blue backpack lost inside the mall.");
        report3.setColor("Blue");
        report3.setBrand("Nike");
        report3.setImageUrl("https://example.com/backpack.jpg");
        report3.setItemDate(LocalDate.now().minusDays(10));
        report3.setStatus(ReportStatus.OPEN);
        report3.setUser(users.get(0));
        report3.setStaff(staff.get(1));
        report3.setCategory(categories.get(2));
        report3.setLocations(Set.of(locations.get(1)));

        Report report4 = new Report();
        report4.setType(ReportType.FOUND);
        report4.setTitle("Found Car Keys");
        report4.setDescription("A set of car keys found at the metro station.");
        report4.setColor("Silver");
        report4.setBrand("Toyota");
        report4.setImageUrl("https://example.com/keys.jpg");
        report4.setItemDate(LocalDate.now().minusDays(20));
        report4.setStatus(ReportStatus.CLOSED);
        report4.setUser(users.get(1));
        report4.setStaff(staff.get(0));
        report4.setCategory(categories.get(3));
        report4.setLocations(Set.of(locations.get(2)));

        reportRepository.saveAll(List.of(
                report1, report2, report3, report4
        ));
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    private Location createLocation(String name, String description, String city, LocationType type) {
        Location location = new Location();
        location.setName(name);
        location.setDescription(description);
        location.setCity(city);
        location.setType(type);
        return location;
    }

    private <T extends BaseAccount> T fillAccount(T account, String fullName, String username,
                                                   String email, String password, String phone) {
        account.setFullName(fullName);
        account.setUsername(username);
        account.setEmail(email);
        account.setPassword(password);
        account.setPhone(phone);
        return account;
    }
}
