/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.misc.ui;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntry;

import com.vaadin.flow.testutil.ChromeBrowserTest;

/**
 * A test class for miscelaneous tests checking features or fixes that do not
 * require their own IT module.
 *
 * Adding new IT modules penalizes build time, otherwise appending tests to this
 * class run new tests faster.
 */
public class MiscelaneousIT extends ChromeBrowserTest {
    @Override
    protected String getTestPath() {
        return "/";
    }

    @Override
    public void setup() throws Exception {
        super.setup();
        open();
    }

    @Test // #5964
    public void should_loadThemedComponent_fromLocal() {
        WebElement body = findElement(By.tagName("body"));
        Assert.assertEquals("2px", body.getCssValue("padding"));
    }

    /**
     * Checks that a missing or incorrect icon is handled properly with an error
     * log and does not halt the whole application startup.
     */
    @Test
    public void handlesIncorrectIconProperly() {
        List<LogEntry> entries = getLogEntries(Level.WARNING);
        List<String> msgs = entries.stream().map(LogEntry::getMessage)
                .filter(msg -> !msg.contains("HTML Imports is deprecated"))
                .collect(Collectors.toList());
        // The icon doesn't exist: it means that it WON'T be handled by the
        // Vaadin servlet at all and
        // there will be an error message: this is expected. But this should be
        // the only error message in the log
        if (msgs.size() >= 1) {
            Assert.assertEquals("Console log messages are: " + msgs, 1,
                    msgs.size());
            Assert.assertTrue(msgs.get(0).contains("Failed to load resource"));
        }

        // regardless of image absence the View should be rendered properly (the
        // icon is handled as a separate request which could have created a
        // separate UI but this doesn't happen: the error in the console is not
        // about the View)
        Assert.assertTrue(
                "Missing/invalid icons at startup should be handled with error log.",
                isElementPresent(By.id(MiscelaneousView.TEST_VIEW_ID)));
    }
}
