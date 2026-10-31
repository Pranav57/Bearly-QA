package com.bearly.qa.tests;

import com.bearly.qa.base.BaseTest;
import com.bearly.qa.base.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

/**
 * Logged-in smoke for CHAT-01, CHAT-04, CHAT-12, CHAT-18 (see docs/02).
 * Needs a test account: -Dbearly.email=... -Dbearly.password=...
 * The selectors are generic guesses (textarea / contenteditable, type=email/password).
 * Inspect the real DOM and pin them to stable data-testid attributes before relying on these tests.
 */
public class ChatSmokeTest extends BaseTest {

    private static final By PROMPT_INPUT = By.cssSelector("textarea, div[contenteditable='true']");

    @BeforeMethod(alwaysRun = true, dependsOnMethods = "setUp")
    public void login() {
        if (Config.email() == null || Config.password() == null) {
            throw new SkipException("Set bearly.email / bearly.password to run authenticated tests");
        }
        driver.get(Config.appUrl());
        WebElement email = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='email'], input[name='email']")));
        email.sendKeys(Config.email());
        List<WebElement> pw = driver.findElements(By.cssSelector("input[type='password']"));
        if (pw.isEmpty()) { // two-step login form
            email.sendKeys(Keys.ENTER);
            pw = List.of(wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[type='password']"))));
        }
        pw.get(0).sendKeys(Config.password(), Keys.ENTER);
        wait.until(ExpectedConditions.visibilityOfElementLocated(PROMPT_INPUT));
    }

    private String sendAndWait(String prompt) {
        int before = bodyText().length();
        WebElement input = driver.findElement(PROMPT_INPUT);
        input.sendKeys(prompt);
        input.sendKeys(Keys.ENTER);
        // Wait until the page text grows, then stops changing (the stream has finished).
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(90))
                .pollingEvery(Duration.ofSeconds(2))
                .until(new java.util.function.Function<org.openqa.selenium.WebDriver, Boolean>() {
                    String last = "";
                    public Boolean apply(org.openqa.selenium.WebDriver d) {
                        String now = bodyText();
                        boolean stable = now.length() > before + prompt.length() + 5 && now.equals(last);
                        last = now;
                        return stable;
                    }
                });
        return bodyText();
    }

    private String bodyText() { return driver.findElement(By.tagName("body")).getText(); }

    @Test(description = "CHAT-01: basic prompt gets a streamed response")
    public void basicPrompt() {
        String page = sendAndWait("Reply with exactly the word PINEAPPLE and nothing else.");
        Assert.assertTrue(page.toUpperCase().split("PINEAPPLE", -1).length - 1 >= 2,
                "Expected the reply to contain PINEAPPLE (prompt + answer)");
        WebElement input = driver.findElement(PROMPT_INPUT);
        String value = input.getAttribute("value");
        String remaining = (value != null ? value : input.getText()).trim();
        Assert.assertEquals(remaining, "", "Input should clear after sending");
    }

    @Test(description = "CHAT-04: multi-turn context is kept")
    public void multiTurnContext() {
        sendAndWait("For this chat, my code name is ZEBRA-42. Just say OK.");
        String page = sendAndWait("What is my code name? Reply with only the code name.");
        int count = page.split("ZEBRA-42", -1).length - 1;
        Assert.assertTrue(count >= 3, "Assistant did not recall the code name (occurrences=" + count + ")");
    }

    @Test(description = "CHAT-12: empty prompt is not sent")
    public void emptyPromptNotSent() {
        String before = bodyText();
        WebElement input = driver.findElement(PROMPT_INPUT);
        input.sendKeys("   ");
        input.sendKeys(Keys.ENTER);
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        Assert.assertEquals(bodyText().replace("   ", ""), before.replace("   ", ""),
                "Whitespace-only prompt should not create a message");
    }

    @Test(description = "CHAT-18: HTML in prompt is rendered as text (no XSS)")
    public void htmlIsEscaped() {
        sendAndWait("Repeat this exactly: <img src=x onerror=\"document.title='XSS'\">");
        Assert.assertNotEquals(driver.getTitle(), "XSS", "Injected HTML executed: XSS vulnerability");
    }
}
