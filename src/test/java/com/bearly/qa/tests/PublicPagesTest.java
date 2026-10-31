package com.bearly.qa.tests;

import com.bearly.qa.base.BaseTest;
import com.bearly.qa.base.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

/** Smoke checks that need no account. Maps to NFR-05/NFR-06/NFR-07 and BILL-05 in docs/02. */
public class PublicPagesTest extends BaseTest {

    @Test(description = "Landing page loads over HTTPS with a Bearly title")
    public void landingPageLoads() {
        long start = System.currentTimeMillis();
        driver.get(Config.baseUrl());
        wait.until(d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
        long ms = System.currentTimeMillis() - start;
        System.out.println("[perf] landing load ms = " + ms);

        Assert.assertTrue(driver.getCurrentUrl().startsWith("https://"), "Must be served over HTTPS");
        Assert.assertTrue(driver.getTitle().toLowerCase().contains("bearly"),
                "Title should mention Bearly, was: " + driver.getTitle());
        Assert.assertTrue(ms < 10_000, "Landing page took " + ms + " ms (>10 s)");
    }

    @Test(description = "Landing page exposes a sign-in / get-started entry point")
    public void hasLoginEntryPoint() {
        driver.get(Config.baseUrl());
        List<WebElement> ctas = wait.until(d -> {
            List<WebElement> els = d.findElements(By.xpath(
                    "//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'log in')"
                    + " or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'sign in')"
                    + " or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'get started')"
                    + " or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'try')]"));
            return els.isEmpty() ? null : els;
        });
        Assert.assertFalse(ctas.isEmpty(), "No login / get-started CTA found");
    }

    @Test(description = "No broken images on the landing page")
    public void noBrokenImages() {
        driver.get(Config.baseUrl());
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        @SuppressWarnings("unchecked")
        List<String> broken = (List<String>) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.images)"
                + ".filter(i => i.complete && i.naturalWidth === 0 && i.src)"
                + ".map(i => i.src);");
        Assert.assertTrue(broken.isEmpty(), "Broken images: " + broken);
    }

    @Test(description = "No severe JS console errors on the landing page")
    public void noSevereConsoleErrors() {
        driver.get(Config.baseUrl());
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        List<String> severe = driver.manage().logs().get(LogType.BROWSER).getAll().stream()
                .filter(e -> e.getLevel().intValue() >= java.util.logging.Level.SEVERE.intValue())
                .map(LogEntry::getMessage)
                .collect(Collectors.toList());
        Assert.assertTrue(severe.isEmpty(), "Severe console errors: " + severe);
    }

    @Test(description = "Layout adapts at 375 px mobile width (no horizontal scroll)")
    public void responsiveMobileWidth() {
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 812));
        driver.get(Config.baseUrl());
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        Long overflow = (Long) ((JavascriptExecutor) driver).executeScript(
                "return document.documentElement.scrollWidth - document.documentElement.clientWidth;");
        Assert.assertTrue(overflow <= 1, "Horizontal overflow of " + overflow + " px at 375 px width");
    }

    @Test(description = "Unknown URL shows a friendly 404, not a stack trace")
    public void friendly404() {
        driver.get(Config.baseUrl() + "/this-page-should-not-exist-qa-12345");
        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertFalse(body.contains("exception") || body.contains("stack trace") || body.contains("at java."),
                "404 page leaks technical details");
    }
}
