package edu.unac.steps;

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
import org.testng.Assert;

public class InventorySteps {
    private ChromeDriver driver;

    @Before
    public void setUp(){
        System.setProperty("webdriver.chrome.driver",
                System.getProperty("user.dir") +
                        "/src/main/java/edu/unac/drivers/chromedriver.exe");//ADD YOUR DRIVER
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        chromeOptions.setBinary("YORCHORMEFOLDER\\chrome.exe");
        driver = new ChromeDriver(chromeOptions);
        driver.manage().window().maximize();
        driver.get("YOURUSERPATH/IdeaProjects/InventoryManagementApplication/Frontend/index.html");
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Given("a device is registered with name Projector, type Multimedia, and location Room101")
    public void a_device_is_registered_with_name_projector_type_multimedia_and_location_room() {
    }

    @Given("the device is currently loaned to user Alice")
    public void the_device_is_currently_loaned_to_user_alice() throws InterruptedException {
    }

    @When("the user attempts to delete the device")
    public void the_user_attempts_to_delete_the_device() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Then("the device should not be deleted")
    public void the_device_should_not_be_deleted() {
    }

    @Then("an error message Failed to delete device should be displayed")
    public void an_error_message_failed_to_delete_device_should_be_displayed() {
    }
}
