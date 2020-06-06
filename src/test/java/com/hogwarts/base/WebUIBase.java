package com.hogwarts.base;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.util.Properties;

/**
 * Created by JiZhi.Qian on 2019/3/11.
 */
public abstract class WebUIBase {
    private Logger logger = Logger.getLogger(WebUIBase.class);
    private String propFileName = "iselenium.properties";

    protected String testcaseName = "";
    protected String curBrowser = "chrome"; //默认浏览器是firefox
    protected WebDriver driver;
    protected WebDriver.Navigation navigation;
    protected String firefoxPath = "";
    protected String chromePath = "";

    protected int waitTime = 15;

    @Before
    public void begin() {
        //加载配置文件，注意需要事先将配置文件放到user.home下
        logger.info("Load properties file:" + propFileName);
        Properties prop = loadFromEnvProperties(propFileName);

        //获取浏览器driver路径
        logger.info("Load webdriver path");
        firefoxPath = prop.getProperty("FIREFOX_PATH");
        chromePath = prop.getProperty("CHROME_PATH");
        logger.info("firefoxPath = " + firefoxPath);
        logger.info("chromePath = " + chromePath);

        //设定当前运行的浏览器
        //需要在环境变量"currentBrowser"中配置当前运行什么浏览器, 可选值"firefox","chrome"
        setCurBrowser();
        logger.info("Current browser is " + curBrowser);

        //构造webdriver
        if (curBrowser.equalsIgnoreCase("firefox")) {
            System.setProperty("webdriver.firefox.bin", firefoxPath);
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
            driver = new FirefoxDriver();
        } else if (curBrowser.equalsIgnoreCase("chrome")) {
            System.setProperty("webdriver.chrome.driver", chromePath);
            driver = new ChromeDriver();
        } else if (curBrowser.equalsIgnoreCase("nogui")) {
            System.setProperty("webdriver.chrome.driver", chromePath);
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless");
            driver = new ChromeDriver(chromeOptions);
        } else {
            System.setProperty("webdriver.firefox.bin", firefoxPath);
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
            driver = new FirefoxDriver();
        }

        WebDriver.Timeouts timeout = driver.manage().timeouts();
        timeout.setScriptTimeout(waitTime, java.util.concurrent.TimeUnit.SECONDS);
        timeout.pageLoadTimeout(waitTime, java.util.concurrent.TimeUnit.SECONDS);
        timeout.implicitlyWait(waitTime, java.util.concurrent.TimeUnit.SECONDS);

        navigation = driver.navigate();
    }

    @After
    public void tearDown() {
        logger.info("Automation test " + testcaseName + " finish!");

        if (driver == null) {
            return;
        }

        driver.quit();
    }

    //加载配置文件
    private Properties loadFromEnvProperties(String propFileName) {
        Properties prop = null;

        String path = System.getProperty("user.home");

        //读入envProperties属性文件
        try {
            prop = new Properties();
            InputStream in = new BufferedInputStream(
                    new FileInputStream(path + File.separator + propFileName));
            prop.load(in);
            in.close();
        } catch (IOException ioex) {
            ioex.printStackTrace();
            logger.error("Load config file fail, please check " + path + " to confirm if the "
                    + propFileName + " file exist!");
        }

        return prop;
    }

    private void setCurBrowser() {
        String value = System.getenv("currentBrowser");
        if (value == null || value.equalsIgnoreCase("")) {
            return;
        }

        if (value.equalsIgnoreCase("firefox") || value.equalsIgnoreCase("chrome")
                || value.equalsIgnoreCase("nogui")) {
            curBrowser = value.toLowerCase();
        }
    }

    protected void wait2s() {
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {

        }
    }
}
