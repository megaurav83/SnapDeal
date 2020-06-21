package testPackage;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class TestSnapDeal {

	public static WebDriver driver;
	public static WebDriverWait wait;
	public static String screenshotPath;
	public static String screenshotName;
	
//Method to find element and if element is not found than wait and retry it
	public static WebElement findElement(By by, int time) {

		WebElement element = null;

		for (int i = 0; i < time; i++) {
			try {
				element = driver.findElement(by);
				break;
			} catch (Exception e) {
				try {
					System.out.println("Waiting for element" + by + " to appear on DOM");
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					System.out.println("Waiting for element" + by + " to appear on DOM");
				}
			}

		}
		return element;
	}
//Method to type in webElement and if element is not ready than wait and retry typing in it
	public static void sendKeys(WebElement ele, int time, String keysToSend) {

		for (int i = 1; i <= time; i++) {

			try {
				ele.sendKeys(keysToSend);
				break;
			} catch (Exception e) {

				try {
					System.out.println("Waiting for element to be ready to enter text");
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					System.out.println("Waiting for element to be ready to enter text");
				}
			}

		}

	}
	//Method to Clicking a webElement and if element is not ready than wait and retry clicking it
	public static void click(WebElement ele, int time) {

		for (int i = 1; i <= time; i++) {

			try {
				ele.click();
				break;
			} catch (Exception e) {

				try {
					/*
					 * System.out.println("Waiting for element " + ele +
					 * "  to be clickable");
					 */
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					System.out.println("Waiting for element " + ele + "  to be clickable");
				}
			}

		}

	}
//Method to select the price Range
	public static void selectPriceRange(String fromRange, String toRange) {
		By fromPriceRangeLocator = By.cssSelector("input[name='fromVal']");
		WebElement fromPriceRange = findElement(fromPriceRangeLocator, 10);

		fromPriceRange.clear();
		sendKeys(fromPriceRange, 10, fromRange);

		By toPriceRangeLocator = By.cssSelector("input[name='toVal']");
		WebElement toPriceRange = findElement(toPriceRangeLocator, 10);

		toPriceRange.clear();
		sendKeys(toPriceRange, 10, toRange);

		By goButtonLocator = By.cssSelector("div[class*='price-go-arrow']");
		WebElement goButton = findElement(goButtonLocator, 10);
		click(goButton, 10);
	}
//Method to MouseHover on an element
	public static Actions moveToElement(By by) {
		WebElement element = findElement(by, 10);
		Actions action = new Actions(driver);
		action.moveToElement(element);
		return action;
	}
//Method to Select multiple Brands
	public static void selectBrand(String filterGroup) throws InterruptedException {
		Thread.sleep(10000);
		wait = new WebDriverWait(driver, 25);
		List<WebElement> filters = driver.findElements(By.cssSelector("div[data-name='" + filterGroup + "'] label"));
		// System.out.println(filters.size()); // for debugging
		for (int i = 0; i < filters.size(); i++) {
			click(filters.get(i), 3);

			// wait for the two overlays to disappear
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.searcharea-overlay")));
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.filterLoader.hidden")));

			// reload the element list after the refresh so you don't get
			// StaleElementExceptions
			filters = driver.findElements(By.cssSelector("div[data-name='" + filterGroup + "'] label"));
		}
	}
//method to select an item, adding it to cart and close the window
	public static void selectItem(By by) throws InterruptedException {
		WebElement item = findElement(by, 10);
		click(item, 10);
		Set<String> windowHandles = driver.getWindowHandles();

		for (String windowHandle : windowHandles) {
			driver.switchTo().window(windowHandle);
		}

		By addToCartLocator = By.xpath("//span[text()='add to cart']");
		WebElement addToCart = findElement(addToCartLocator, 5);
		click(addToCart, 5);
		Thread.sleep(5000);
		driver.close();
	}
//method to capture the screenshot
	public static String captureScreenshot() throws IOException {

		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

		Date d = new Date();
		screenshotName = d.toString().replace(":", "_").replace(" ", "_") + ".jpg";
		screenshotPath = System.getProperty("user.dir") + "\\target\\" + screenshotName;
		File dstFile = new File(screenshotPath);

		FileUtils.copyFile(scrFile, dstFile);
		return screenshotPath;

	}

	public static void main(String[] args) throws InterruptedException, IOException {
		

		ChromeOptions option = new ChromeOptions();
		option.addArguments("start-maximized");
		option.setAcceptInsecureCerts(true);
		option.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
		WebDriverManager.chromedriver().setup();// Get the WebDriverManager
												// dependency to setup the
												// ChromeDriver path
		driver = new ChromeDriver(option);

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		// Launching the URL
		driver.get("https://www.snapdeal.com");
		// Moving to Mobile and tablet option
		By mobileAndTabletLocator = By.xpath("//span[contains(text(),'Mobile & Tablets')]");
		moveToElement(mobileAndTabletLocator).perform();

		// Moving to and Selecting Smart Phones
		By smartPhonesLocator = By.xpath("//span[contains(text(),'Smartphones')]");
		moveToElement(smartPhonesLocator).click().perform();

		// Selecting Price Range
		selectPriceRange("10000", "20000");

		// Selecting Brands
		selectBrand("Brand");
		// Capturing the windowHandle of Product WebPage
		String productSelectionWindow = driver.getWindowHandle();

		// Finding the First Product
		By firstProductLocator = By.xpath("//div[@id='products']/section[1]/div[4]/div[3]/div[1]/a/p");
		WebElement firstProduct = findElement(firstProductLocator, 10);

		// Finding the selecting the first product again to avoid StaleElement
		// exception and adding it to cart
		selectItem(firstProductLocator);
		// Switching to main Product WebPage
		driver.switchTo().window(productSelectionWindow);

		// Selecting the adding second product to cart

		By secondProductLocator = By.xpath("//div[@id='products']/section[1]/div[2]/div[3]/div[1]/a/p");
		selectItem(secondProductLocator);
		// Switching to main Product WebPage
		driver.switchTo().window(productSelectionWindow);
		// Selecting the adding Third product to cart
		By thirdProductLocator = By.xpath("//div[@id='products']/section[1]/div[3]/div[3]/div[1]/a/p");
		selectItem(thirdProductLocator);
		// Switching to main Product WebPage
		driver.switchTo().window(productSelectionWindow);

		// Refreshing the page so that cart is updated with the items number
		driver.navigate().refresh();
		driver.navigate().refresh();

		// Clicking on cart option
		By cartLocator = By.cssSelector("i.sd-icon.sd-icon-cart-icon-white-2");
		WebElement cart = findElement(cartLocator, 10);
		click(cart, 10);

		// Removing the item from the cart
		By removeButtonLocator = By.xpath("//div[@class='remove-item-div'][1]");

		WebElement removeButton = findElement(removeButtonLocator, 5);
		click(removeButton, 5);

		// Waiting until the message that item is removed are displayed
		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p.lfloat.alert-text")));
		// Capturing the Screenshot
		String path = captureScreenshot();
		// Printing the path of screenshot file
		System.out.println("Get the screenshot at following path" + path);

		// Clicking on Proceed to pay button
		By proceedToPayLocator = By.cssSelector("input.btn.btn-xl.rippleWhite.cart-button");
		WebElement proceedToPayButton = findElement(proceedToPayLocator, 5);
		click(proceedToPayButton, 5);

		// Quitting the driver instance
		driver.quit();
	}

}
