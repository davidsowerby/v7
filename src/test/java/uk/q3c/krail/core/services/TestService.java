/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.core.services;

import uk.q3c.krail.core.eventbus.GlobalBusProvider;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;

public class TestService extends AbstractService {

    @Dependency
    Service serviceA;

    @Dependency(optional = true)
    Service serviceB;

    @Dependency(always = false)
    Service serviceC;

    @Dependency(optional = true, always = false)
    Service serviceD;

    protected TestService(Translate translate, Service serviceA, Service serviceB, Service serviceC, Service serviceD,
                          GlobalBusProvider globalBusProvider, RelatedServicesExecutor servicesExecutor) {
        super(translate, globalBusProvider, servicesExecutor);
        this.serviceA = serviceA;
        this.serviceB = serviceB;
        this.serviceC = serviceC;
        this.serviceD = serviceD;
    }

    @Override
    public I18NKey getNameKey() {
        return LabelKey.Yes;
    }

    @Override
    protected void doStop() throws Exception {

    }

    @Override
    protected void doStart() throws Exception {

    }
}