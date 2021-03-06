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
package fixture.testviews2

import com.vaadin.ui.Component
import com.vaadin.ui.Label
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.core.view.NavigationStateExt
import uk.q3c.krail.i18n.I18NKey

class ViewB121 : KrailView {
    private val label = Label("not used")
    override var rootComponent: Component = label

    override fun beforeBuild(navigationStateExt: NavigationStateExt) {
        TODO()
    }

    override fun buildView() {
        TODO()
    }

    override fun afterBuild() {
        TODO()
    }



    override fun init() {}


    override fun getNameKey(): I18NKey? {
        return null
    }

    override fun setNameKey(nameKey: I18NKey) {

    }

    override fun getDescriptionKey(): I18NKey? {
        return null
    }

    override fun setDescriptionKey(descriptionKey: I18NKey) {

    }

    override fun getName(): String? {
        return null
    }

    override fun getDescription(): String? {
        return null
    }
}
