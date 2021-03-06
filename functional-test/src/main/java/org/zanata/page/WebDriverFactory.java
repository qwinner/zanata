/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.page;

import static org.zanata.util.Constants.chrome;
import static org.zanata.util.Constants.firefox;
import static org.zanata.util.Constants.loadProperties;
import static org.zanata.util.Constants.webDriverType;
import static org.zanata.util.Constants.zanataInstance;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum WebDriverFactory
{
   INSTANCE;

   private WebDriver driver;
   private Properties properties;

   public WebDriver getDriver()
   {
      if (driver == null)
      {
         synchronized (this)
         {
            if (driver == null)
            {
               properties = loadProperties();
               driver = createDriver();
               driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
               Runtime.getRuntime().addShutdownHook(new ShutdownHook());
            }
         }
      }
      return driver;
   }

   public String getHostUrl()
   {
      if (properties == null || driver == null)
      {
         getDriver();
      }
      return properties.getProperty(zanataInstance.value());
   }

   private WebDriver createDriver()
   {
      String driverType = properties.getProperty(webDriverType.value(), "htmlUnit");
      if (driverType.equalsIgnoreCase(chrome.value()))
      {
         return configureChromeDriver();
      }
      else if (driverType.equalsIgnoreCase(firefox.value()))
      {
         return configureFirefoxDriver();
      }
      else
      {
         return configureHtmlDriver();
      }
   }

   private WebDriver configureHtmlDriver()
   {
      return new HtmlUnitDriver(true);
   }

   private WebDriver configureChromeDriver()
   {
      DesiredCapabilities capabilities = DesiredCapabilities.chrome();
      capabilities.setCapability("chrome.binary", "/opt/google/chrome/google-chrome");
      return new ChromeDriver(capabilities);
   }

   private WebDriver configureFirefoxDriver()
   {
      final String pathToFirefox = Strings.emptyToNull(properties.getProperty("firefox.path"));

      FirefoxBinary firefoxBinary = null;
      if (pathToFirefox != null)
      {
         firefoxBinary = new FirefoxBinary(new File(pathToFirefox));
      }
      else
      {
         firefoxBinary = new FirefoxBinary();
      }
      //we timeout the connection in 10 seconds
//      firefoxBinary.setTimeout(SECONDS.toMillis(10));

      return new FirefoxDriver(firefoxBinary, makeFirefoxProfile());
//      return new FirefoxDriver();
   }

   private FirefoxProfile makeFirefoxProfile()
   {
      if (!Strings.isNullOrEmpty(System.getProperty("webdriver.firefox.profile")))
      {
         throw new RuntimeException("webdriver.firefox.profile is ignored");
         // TODO - look at FirefoxDriver.getProfile().
      }
      final FirefoxProfile firefoxProfile = new FirefoxProfile();
      // firefoxProfile.setPreference("browser.safebrowsing.malware.enabled",
      // false); // disables connection to sb-ssl.google.com
      firefoxProfile.setAlwaysLoadNoFocusLib(true);
      firefoxProfile.setEnableNativeEvents(true);
      firefoxProfile.setAcceptUntrustedCertificates(true);
//      firefoxProfile.setPort(8000);
      return firefoxProfile;
   }

   private class ShutdownHook extends Thread
   {
      public void run()
      {
         // If webdriver is running quit.
         WebDriver driver = getDriver();
         if (driver != null)
         {
            try
            {
               log.info("Quitting webdriver.");
               driver.quit();
            }
            catch (Throwable e)
            {
               // Ignoring driver tear down errors.
            }
         }
      }
   }
}
