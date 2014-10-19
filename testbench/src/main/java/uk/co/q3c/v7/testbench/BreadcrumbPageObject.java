/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.co.q3c.v7.testbench;

import com.google.common.base.Optional;
import uk.co.q3c.v7.base.view.component.DefaultBreadcrumb;
import uk.co.q3c.v7.base.view.component.NavigationButton;

/**
 * Created by david on 04/10/14.
 */
public class BreadcrumbPageObject extends PageElement<DefaultBreadcrumbElement> {
    /**
     * Test object to represent a {@link DefaultBreadcrumb}
     *
     * @param parentCase
     */
    public BreadcrumbPageObject(V7TestBenchTestCase parentCase) {
        super(parentCase, DefaultBreadcrumbElement.class, Optional.absent(), DefaultBreadcrumb.class);
    }

    public ButtonPageElement button(int index) {
        return new ButtonPageElement(parentCase, Optional.of(index), DefaultBreadcrumb.class, NavigationButton.class);
    }
}
