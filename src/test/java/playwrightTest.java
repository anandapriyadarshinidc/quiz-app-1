package com.example;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

public class playwrightTest {

    @Test
    void quizapp() {

        try (Playwright playwright = Playwright.create()) {

            // =====================================================
            // 1. LAUNCH CHROMIUM
            // =====================================================

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
            );

            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            System.out.println("Browser launched.");

            // =====================================================
            // 2. OPEN QUIZ APPLICATION
            // =====================================================

            page.navigate("http://localhost:8090/quizapp/");

            System.out.println("Quiz application opened.");

            // Wait so you can see the Categories screen
            page.waitForTimeout(3000);

            // =====================================================
            // 3. SELECT CATEGORY
            // =====================================================

            System.out.println("Selecting category...");

            page.locator("#quizap__Categories__el_btn_3_0").click();

            System.out.println("Category selected.");

            // Wait for Questions screen
            page.waitForTimeout(3000);

            // =====================================================
            // 4. ANSWER QUESTION 1
            // =====================================================

            System.out.println("Answering Question 1...");

            page.locator("#quizap__Questions__el_inp_1_0").click();
            page.locator("#quizap__Questions__el_inp_1_0").fill("A");

            page.waitForTimeout(1500);

            // =====================================================
            // 5. ANSWER QUESTION 2
            // =====================================================

            System.out.println("Answering Question 2...");

            page.locator("#quizap__Questions__el_inp_1_1").click();
            page.locator("#quizap__Questions__el_inp_1_1").fill("B");

            page.waitForTimeout(1500);

            // =====================================================
            // 6. ANSWER QUESTION 3
            // =====================================================

            System.out.println("Answering Question 3...");

            page.locator("#quizap__Questions__el_inp_1_2").click();
            page.locator("#quizap__Questions__el_inp_1_2").fill("C");

            page.waitForTimeout(1500);

            // =====================================================
            // 7. ANSWER QUESTION 4
            // =====================================================

            System.out.println("Answering Question 4...");

            page.locator("#quizap__Questions__el_inp_1_3").click();
            page.locator("#quizap__Questions__el_inp_1_3").fill("D");

            page.waitForTimeout(1500);

            // =====================================================
            // 8. ANSWER QUESTION 5
            // =====================================================

            System.out.println("Answering Question 5...");

            page.locator("#quizap__Questions__el_inp_1_4").click();
            page.locator("#quizap__Questions__el_inp_1_4").fill("A");

            page.waitForTimeout(2000);

            System.out.println("All answers entered.");

            // =====================================================
            // 9. SUBMIT QUIZ
            // =====================================================

            System.out.println("Clicking Submit...");

            page.getByRole(
                    AriaRole.BUTTON,
                    new Page.GetByRoleOptions()
                            .setName("Submit")
            ).click();

            System.out.println("Submit clicked.");

            // Wait for confirmation popup
            page.waitForTimeout(3000);

            // =====================================================
            // 10. CLICK OK
            // =====================================================

            System.out.println("Clicking OK...");

            page.getByRole(
                    AriaRole.BUTTON,
                    new Page.GetByRoleOptions()
                            .setName("Ok")
            ).click();

            System.out.println("OK clicked.");

            // Wait for next screen
            page.waitForTimeout(3000);

            // =====================================================
            // 11. VIEW RESULTS
            // =====================================================

            System.out.println("Clicking View Results...");

            page.getByRole(
                    AriaRole.BUTTON,
                    new Page.GetByRoleOptions()
                            .setName("view results")
            ).click();

            System.out.println("View Results clicked.");

            // Give result screen time to load
            page.waitForTimeout(5000);

            // =====================================================
            // 12. PRINT CURRENT PAGE INFORMATION
            // =====================================================

            System.out.println("------------------------------------");
            System.out.println("RESULT SCREEN");
            System.out.println("------------------------------------");

            System.out.println(
                    "Current URL: " + page.url()
            );

            System.out.println(
                    "Page title: " + page.title()
            );

            // =====================================================
            // 13. KEEP RESULT PAGE OPEN
            // =====================================================

            System.out.println("------------------------------------");
            System.out.println("Result page is open.");
            System.out.println("Browser will close after 30 seconds.");
            System.out.println("------------------------------------");

            page.waitForTimeout(3000);

            // =====================================================
            // 14. CLOSE BROWSER
            // =====================================================

            browser.close();

            System.out.println("Browser closed.");
            System.out.println("Quiz test completed.");

        }
    }
}