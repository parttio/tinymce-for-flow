package org.vaadin.tinymce;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import in.virit.mopo.Mopo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MopoSmokeTest {

    @LocalServerPort
    private int port;

    static Playwright playwright = Playwright.create();

    private Browser browser;
    private Page page;
    private Mopo mopo;

    @BeforeEach
    public void setup() {
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions()
//                        .setHeadless(false)
//                        .setDevtools(true)
                );

        page = browser.newPage();
        page.setDefaultTimeout(5000); // die faster if needed
        mopo = new Mopo(page);
    }

    @AfterEach
    public  void closePlaywright() {
        page.close();
        browser.close();
    }

    @Test
    public void smokeTest() {
        String rootUrl = "http://localhost:" + port + "/";
        mopo.getViewsReportedByDevMode(browser, rootUrl).forEach(viewName -> {
            String url = rootUrl + viewName;
            page.navigate(url);
            mopo.assertNoJsErrors();
            System.out.println("Checked %s and it contained no JS errors.".formatted(viewName));
        });

    }

    @Test
    public void menuConfig() throws InterruptedException {
        String url = "http://localhost:" + port + "/menuconfig";
        page.navigate(url);
        PlaywrightAssertions.assertThat(page.locator("button").getByText("File")).isVisible();
        Assertions.assertEquals(0, page.locator("button").getByText("Insert").count());
    }

}
