package edu.unac.steps;

import edu.unac.repository.DeviceRepository;
import edu.unac.repository.LoanRepository;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import java.util.List;

public class InventorySteps {

    private ChromeDriver driver;

    @Before
    public void setUp(){

        System.setProperty("webdriver.chrome.driver",
                System.getProperty("user.dir") +
                        "/src/main/java/edu/unac/drivers/chromedriver.exe");//ADD YOUR DRIVER
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        chromeOptions.setBinary("C:\\Users\\super\\Downloads\\chrome-win64\\chrome-win64\\chrome.exe");
        driver = new ChromeDriver(chromeOptions);
        driver.manage().window().maximize();
        driver.get("C:\\Users\\super\\Downloads\\InventoryManagementApplication\\InventoryManagementApplication\\Frontend\\index.html");
    }

    @After
    public void tearDown() throws InterruptedException {
        Thread.sleep(2000);
        driver.quit();
    }

    @Given("a device is registered with name Projector, type Multimedia, and location Room101")
    public void a_device_is_registered_with_name_projector_type_multimedia_and_location_room() {
        WebElement nameTextBox = driver.findElement(By.id("deviceName"));
        WebElement typeTextBox = driver.findElement(By.id("deviceType"));
        WebElement locationTextBox = driver.findElement(By.id("deviceLocation"));
        nameTextBox.sendKeys("Projector");
        typeTextBox.sendKeys("Multimedia");
        locationTextBox.sendKeys("Room101");

        WebElement addButtonDevice = driver.findElement(By.id("addDeviceBtn"));
        addButtonDevice.click();
    }

    @Given("the device is currently loaned to user Alice")
    public void the_device_is_currently_loaned_to_user_alice() throws InterruptedException {
        Thread.sleep(1000);
        WebElement dropdown = driver.findElement(By.id("loanDeviceSelect"));
        Select dropdownSelect = new Select(dropdown);
        dropdownSelect.selectByIndex(0);

        WebElement borrowerTextBox = driver.findElement(By.id("loanBorrowedBy"));
        borrowerTextBox.sendKeys("Alice");

        WebElement addButtonLoan = driver.findElement(By.id("addLoanBtn"));
        addButtonLoan.click();
    }

    @When("the user attempts to delete the device")
    public void the_user_attempts_to_delete_the_device() throws InterruptedException {
        Thread.sleep(1000);
        WebElement deleteButtonLoan = driver.findElement(By.xpath("//*[@id=\"devicesTableBody\"]/tr/td[6]/button"));
        deleteButtonLoan.click();
    }

    @Then("the device should not be deleted")
    public void the_device_should_not_be_deleted() throws InterruptedException {
        Thread.sleep(1000);
        WebElement dropdown = driver.findElement(By.id("loanDeviceSelect"));
        Select dropdownSelect = new Select(dropdown);
        List<WebElement> options = dropdownSelect.getOptions();

        Assert.assertTrue(options.size() <= 1,
                "The selector was expected to be empty.");
    }

    @Then("an error message Failed to delete device should be displayed")
    public void an_error_message_failed_to_delete_device_should_be_displayed() throws InterruptedException {
        Thread.sleep(2000);
        WebElement containerMessage = driver.findElement(By.id("deviceMessage"));
        String message = containerMessage.getText();
        Assert.assertEquals("Failed to delete device", message,
                "The error message does not match what was expected.\n.");
    }
}
